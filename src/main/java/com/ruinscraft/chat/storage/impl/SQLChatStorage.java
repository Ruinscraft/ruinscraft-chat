package com.ruinscraft.chat.storage.impl;

import com.google.common.collect.Lists;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.DirectMessage;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.player.PersonalizationSettings;
import com.ruinscraft.chat.storage.ChatStorage;
import com.ruinscraft.chat.storage.query.*;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public abstract class SQLChatStorage extends ChatStorage {

    private ChatPlugin chatPlugin;

    public SQLChatStorage(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    private final class Table {
        public static final String CHAT_PLAYERS = "chat_players";
        public static final String ONLINE_CHAT_PLAYERS = "online_chat_players";
        public static final String CHAT_MESSAGES = "chat_messages";
        public static final String FRIEND_REQUESTS = "friend_requests";
        public static final String MAIL_MESSAGES = "mail_messages";
        public static final String BLOCKED_PLAYERS = "blocked_players";
        public static final String FOCUSED_CHANNELS = "focused_channels";
        public static final String PERSONALIZATION_SETTINGS = "personalization_settings";
    }

    protected void createTables() {
        try (Connection connection = createConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.CHAT_PLAYERS + " (id VARCHAR(36), username VARCHAR(16), first_seen BIGINT, last_seen BIGINT, PRIMARY KEY (id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.ONLINE_CHAT_PLAYERS + " (id VARCHAR(36), logged_in_at BIGINT, updated_at BIGINT, server_name VARCHAR(32), group_name VARCHAR(32), vanished BOOL, last_dm VARCHAR(36), PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES " + Table.CHAT_PLAYERS + "(id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.CHAT_MESSAGES + " (id VARCHAR(36), server_id VARCHAR(36), channel VARCHAR(64), time BIGINT, sender_id VARCHAR(36), content VARCHAR(255), PRIMARY KEY (id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.FRIEND_REQUESTS + " (requester_id VARCHAR(36), target_id VARCHAR(36), time BIGINT, accepted BOOL, FOREIGN KEY (requester_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (target_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), UNIQUE KEY friend (requester_id, target_id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.MAIL_MESSAGES + " (id VARCHAR(36), sender_id VARCHAR(36), recipient_id VARCHAR(36), time BIGINT, is_read BOOL, content VARCHAR(255), PRIMARY KEY (id), FOREIGN KEY (sender_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (recipient_id) REFERENCES " + Table.CHAT_PLAYERS + "(id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.BLOCKED_PLAYERS + " (blocker_id VARCHAR(36), blocked_id VARCHAR(36), FOREIGN KEY (blocker_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (blocked_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), UNIQUE KEY block (blocker_id, blocked_id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.FOCUSED_CHANNELS + " (id VARCHAR(36), plugin_name VARCHAR(32), channel_name VARCHAR(32), FOREIGN KEY (id) REFERENCES " + Table.CHAT_PLAYERS + "(id), UNIQUE KEY focused (id, plugin_name));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.PERSONALIZATION_SETTINGS + " (id VARCHAR(36), name_color VARCHAR(16), nickname VARCHAR(64), hide_profanity BOOL, allow_dms_from_anyone BOOL, silent_join_leave BOOL, muted_channel_db_names VARCHAR(255), PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES " + Table.CHAT_PLAYERS + "(id));");
                statement.executeBatch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> saveChatPlayer(ChatPlayer chatPlayer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement update = connection.prepareStatement(
                        "INSERT INTO " + Table.CHAT_PLAYERS + " (id, username, first_seen, last_seen) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, last_seen = ?;")) {
                    update.setString(1, chatPlayer.getMojangId().toString());
                    update.setString(2, chatPlayer.getMinecraftUsername());
                    update.setLong(3, chatPlayer.getFirstSeen());
                    update.setLong(4, chatPlayer.getLastSeen());
                    update.setString(5, chatPlayer.getMinecraftUsername());
                    update.setLong(6, chatPlayer.getLastSeen());
                    update.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private CompletableFuture<ChatPlayerQuery> queryChatPlayer(String conditional, String data) {
        return CompletableFuture.supplyAsync(() -> {
            ChatPlayerQuery chatPlayerQuery = new ChatPlayerQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.CHAT_PLAYERS + " WHERE " + conditional + " = ?;")) {
                    query.setString(1, data);

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID mojangId = UUID.fromString(resultSet.getString("id"));
                            String username = resultSet.getString("username");
                            long firstSeen = resultSet.getLong("first_seen");
                            long lastSeen = resultSet.getLong("last_seen");
                            ChatPlayer chatPlayer = new ChatPlayer(mojangId, username, firstSeen, lastSeen);
                            chatPlayerQuery.addResult(chatPlayer);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return chatPlayerQuery;
        });
    }

    @Override
    public CompletableFuture<ChatPlayerQuery> queryChatPlayer(UUID mojangId) {
        return queryChatPlayer("id", mojangId.toString());
    }

    @Override
    public CompletableFuture<ChatPlayerQuery> queryChatPlayer(String username) {
        return queryChatPlayer("username", username);
    }

    @Override
    public CompletableFuture<Void> saveChatMessage(ChatMessage chatMessage) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement insert = connection.prepareStatement(
                        "INSERT INTO " + Table.CHAT_MESSAGES + " (id, server_id, channel, time, sender_id, content) VALUES (?, ?, ?, ?, ?, ?);")) {
                    insert.setString(1, chatMessage.getId().toString());
                    insert.setString(2, chatMessage.getOriginServerId().toString());
                    insert.setString(3, chatMessage.getChannelDbName());
                    insert.setLong(4, chatMessage.getTime());
                    insert.setString(5, chatMessage.getSender().getMojangId().toString());
                    insert.setString(6, chatMessage.getContent());
                    insert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<ChatMessageQuery> queryChatMessage(UUID chatMessageId) {
        return CompletableFuture.supplyAsync(() -> {
            ChatMessageQuery chatMessageQuery = new ChatMessageQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.CHAT_MESSAGES + " WHERE id = ?;")) {
                    query.setString(1, chatMessageId.toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID serverId = UUID.fromString(resultSet.getString("server_id"));
                            String channelDbName = resultSet.getString("channel");
                            String pluginName = channelDbName.split(":")[0];
                            String channelName = channelDbName.split(":")[1];
                            long time = resultSet.getLong("time");
                            UUID senderId = UUID.fromString(resultSet.getString("sender_id"));
                            String content = resultSet.getString("content");
                            ChatPlayer sender = chatPlugin.getChatPlayerManager().getAndLoad(senderId);

                            if (pluginName.startsWith("dm")) {
                                UUID recipientId = UUID.fromString(channelName);
                                ChatPlayer recipient = chatPlugin.getChatPlayerManager().getAndLoad(recipientId);
                                DirectMessage directMessage = new DirectMessage(chatMessageId, time, sender, content, serverId, channelDbName, recipient);
                                chatMessageQuery.addResult(directMessage);
                            } else {
                                ChatMessage chatMessage = new ChatMessage(chatMessageId, time, sender, content, serverId, channelDbName);
                                chatMessageQuery.addResult(chatMessage);
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return chatMessageQuery;
        });
    }

    @Override
    public CompletableFuture<Void> saveOnlineChatPlayer(OnlineChatPlayer chatPlayer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + Table.ONLINE_CHAT_PLAYERS + " (id, logged_in_at, updated_at, server_name, group_name, vanished, last_dm) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE updated_at = ?, server_name = ?, group_name = ?, vanished = ?, last_dm = ?;")) {
                    upsert.setString(1, chatPlayer.getMojangId().toString());
                    upsert.setLong(2, chatPlayer.getLoggedInAt());
                    upsert.setLong(3, chatPlayer.getUpdatedAt());
                    upsert.setString(4, chatPlayer.getServerName());
                    upsert.setString(5, chatPlayer.getGroupName());
                    upsert.setBoolean(6, chatPlayer.isVanished());
                    if (chatPlayer.getLastDm() != null) {
                        upsert.setString(7, chatPlayer.getLastDm().toString());
                    } else {
                        upsert.setString(7, null);
                    }
                    upsert.setLong(8, chatPlayer.getUpdatedAt());
                    upsert.setString(9, chatPlayer.getServerName());
                    upsert.setString(10, chatPlayer.getGroupName());
                    upsert.setBoolean(11, chatPlayer.isVanished());
                    if (chatPlayer.getLastDm() != null) {
                        upsert.setString(12, chatPlayer.getLastDm().toString());
                    } else {
                        upsert.setString(12, null);
                    }
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<OnlineChatPlayerQuery> queryOnlineChatPlayer(UUID mojangId) {
        return CompletableFuture.supplyAsync(() -> {
            OnlineChatPlayerQuery onlineChatPlayerQuery = new OnlineChatPlayerQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.ONLINE_CHAT_PLAYERS + " WHERE id = ?;")) {
                    query.setString(1, mojangId.toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getAndLoad(mojangId);
                            long loggedInAt = resultSet.getLong("logged_in_at");
                            long updatedAt = resultSet.getLong("updated_at");
                            String serverName = resultSet.getString("server_name");
                            String groupName = resultSet.getString("group_name");
                            boolean vanished = resultSet.getBoolean("vanished");
                            UUID lastDm = null;
                            if (resultSet.getString("last_dm") != null) {
                                lastDm = UUID.fromString(resultSet.getString("last_dm"));
                            }
                            OnlineChatPlayer onlineChatPlayer = new OnlineChatPlayer(chatPlayer, loggedInAt, updatedAt, serverName, groupName, vanished, lastDm);
                            onlineChatPlayerQuery.addResult(onlineChatPlayer);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return onlineChatPlayerQuery;
        });
    }

    @Override
    public CompletableFuture<OnlineChatPlayerQuery> queryOnlineChatPlayers() {
        return CompletableFuture.supplyAsync(() -> {
            OnlineChatPlayerQuery onlineChatPlayerQuery = new OnlineChatPlayerQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.ONLINE_CHAT_PLAYERS + ";")) {
                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID mojangId = UUID.fromString(resultSet.getString("id"));
                            ChatPlayer chatPlayer = queryChatPlayer(mojangId).join().getFirst();
                            long loggedInAt = resultSet.getLong("logged_in_at");
                            long updatedAt = resultSet.getLong("updated_at");
                            String serverName = resultSet.getString("server_name");
                            String groupName = resultSet.getString("group_name");
                            boolean vanished = resultSet.getBoolean("vanished");
                            UUID lastDm = null;
                            if (resultSet.getString("last_dm") != null) {
                                lastDm = UUID.fromString(resultSet.getString("last_dm"));
                            }
                            OnlineChatPlayer onlineChatPlayer = new OnlineChatPlayer(chatPlayer, loggedInAt, updatedAt, serverName, groupName, vanished, lastDm);
                            onlineChatPlayerQuery.addResult(onlineChatPlayer);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return onlineChatPlayerQuery;
        });
    }

    @Override
    public CompletableFuture<Void> saveFriendRequest(FriendRequest friendRequest) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + Table.FRIEND_REQUESTS + " (requester_id, target_id, time, accepted) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE accepted = ?;")) {
                    upsert.setString(1, friendRequest.getRequester().getMojangId().toString());
                    upsert.setString(2, friendRequest.getTarget().getMojangId().toString());
                    upsert.setLong(3, friendRequest.getTime());
                    upsert.setBoolean(4, friendRequest.isAccepted());
                    upsert.setBoolean(5, friendRequest.isAccepted());
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteFriendRequest(FriendRequest friendRequest) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM " + Table.FRIEND_REQUESTS + " WHERE requester_id = ? AND target_id = ?;")) {
                    delete.setString(1, friendRequest.getRequester().getMojangId().toString());
                    delete.setString(2, friendRequest.getTarget().getMojangId().toString());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<FriendRequestQuery> queryFriendRequests(OnlineChatPlayer onlineChatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            FriendRequestQuery friendRequestQuery = new FriendRequestQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.FRIEND_REQUESTS + " WHERE requester_id = ? OR target_id = ?;")) {
                    query.setString(1, onlineChatPlayer.getMojangId().toString());
                    query.setString(2, onlineChatPlayer.getMojangId().toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID requesterId = UUID.fromString(resultSet.getString("requester_id"));
                            UUID targetId = UUID.fromString(resultSet.getString("target_id"));
                            long time = resultSet.getLong("time");
                            boolean accepted = resultSet.getBoolean("accepted");
                            ChatPlayer requester = chatPlugin.getChatPlayerManager().getAndLoad(requesterId);
                            ChatPlayer target = chatPlugin.getChatPlayerManager().getAndLoad(targetId);
                            FriendRequest friendRequest = new FriendRequest(requester, target, time, accepted);
                            friendRequestQuery.addResult(friendRequest);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return friendRequestQuery;
        });
    }

    @Override
    public CompletableFuture<Void> deleteOfflineChatPlayers() {
        long thresholdTime = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(OnlineChatPlayer.SECONDS_UNTIL_OFFLINE);

        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM " + Table.ONLINE_CHAT_PLAYERS + " WHERE updated_at < ?;")) {
                    delete.setLong(1, thresholdTime);
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveMailMessage(MailMessage mailMessage) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + Table.MAIL_MESSAGES + " (id, sender_id, recipient_id, time, is_read, content) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE is_read = ?;")) {
                    insert.setString(1, mailMessage.getId().toString());
                    insert.setString(2, mailMessage.getSender().getMojangId().toString());
                    insert.setString(3, mailMessage.getRecipient().getMojangId().toString());
                    insert.setLong(4, mailMessage.getTime());
                    insert.setBoolean(5, mailMessage.isRead());
                    insert.setString(6, mailMessage.getContent());
                    insert.setBoolean(7, mailMessage.isRead());
                    insert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<MailMessageQuery> queryMailMessages(ChatPlayer chatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            MailMessageQuery mailMessageQuery = new MailMessageQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.MAIL_MESSAGES + " WHERE recipient_id = ? AND is_read = ?;")) {
                    query.setString(1, chatPlayer.getMojangId().toString());
                    query.setBoolean(2, false);

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID id = UUID.fromString(resultSet.getString("id"));
                            UUID senderId = UUID.fromString(resultSet.getString("sender_id"));
                            long time = resultSet.getLong("time");
                            boolean read = resultSet.getBoolean("is_read");
                            String content = resultSet.getString("content");
                            ChatPlayer sender = chatPlugin.getChatPlayerManager().getAndLoad(senderId);
                            MailMessage mailMessage = new MailMessage(id, time, sender, content, chatPlayer, read);
                            mailMessageQuery.addResult(mailMessage);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return mailMessageQuery;
        });
    }

    @Override
    public CompletableFuture<ChatPlayerQuery> queryBlocked(ChatPlayer chatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            ChatPlayerQuery chatPlayerQuery = new ChatPlayerQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.BLOCKED_PLAYERS + " WHERE blocker_id = ?;")) {
                    query.setString(1, chatPlayer.getMojangId().toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID blockedId = UUID.fromString(resultSet.getString("blocked_id"));
                            ChatPlayer blockedChatPlayer = chatPlugin.getChatPlayerManager().getAndLoad(blockedId);
                            chatPlayerQuery.addResult(blockedChatPlayer);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return chatPlayerQuery;
        });
    }

    @Override
    public CompletableFuture<Void> insertBlock(ChatPlayer blocker, ChatPlayer blocked) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + Table.BLOCKED_PLAYERS + " (blocker_id, blocked_id) VALUES (?, ?);")) {
                    insert.setString(1, blocker.getMojangId().toString());
                    insert.setString(2, blocked.getMojangId().toString());
                    insert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteBlock(ChatPlayer blocker, ChatPlayer blocked) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM " + Table.BLOCKED_PLAYERS + " WHERE blocker_id = ? AND blocked_id = ?;")) {
                    delete.setString(1, blocker.getMojangId().toString());
                    delete.setString(2, blocked.getMojangId().toString());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> insertActiveChannel(ChatPlayer chatPlayer, ChatChannel channel) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement insert = connection.prepareStatement("INSERT INTO " + Table.FOCUSED_CHANNELS + " (id, plugin_name, channel_name) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE channel_name = ?;")) {
                    insert.setString(1, chatPlayer.getMojangId().toString());
                    insert.setString(2, channel.getPluginName());
                    insert.setString(3, channel.getName());
                    insert.setString(4, channel.getName());
                    insert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> deleteActiveChannel(ChatPlayer chatPlayer, ChatChannel channel) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM " + Table.FOCUSED_CHANNELS + " WHERE id = ? AND plugin_name = ? AND channel_name = ?;")) {
                    delete.setString(1, chatPlayer.getMojangId().toString());
                    delete.setString(2, channel.getPluginName());
                    delete.setString(3, channel.getName());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<FocusedChatChannelNameQuery> queryFocusedChannels(ChatPlayer chatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            FocusedChatChannelNameQuery focusedChatChannelNameQuery = new FocusedChatChannelNameQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.FOCUSED_CHANNELS + " WHERE id = ?;")) {
                    query.setString(1, chatPlayer.getMojangId().toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            String pluginName = resultSet.getString("plugin_name");
                            String channelName = resultSet.getString("channel_name");
                            String channelDbName = pluginName + ":" + channelName;
                            focusedChatChannelNameQuery.addResult(channelDbName);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return focusedChatChannelNameQuery;
        });
    }

    @Override
    public CompletableFuture<Void> savePersonalizationSettings(ChatPlayer chatPlayer, PersonalizationSettings personalizationSettings) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + Table.PERSONALIZATION_SETTINGS + " (id, name_color, nickname, hide_profanity, allow_dms_from_anyone, silent_join_leave, muted_channel_db_names) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name_color = ?, nickname = ?, hide_profanity = ?, allow_dms_from_anyone = ?, silent_join_leave = ?, muted_channel_db_names = ?;")) {
                    upsert.setString(1, chatPlayer.getMojangId().toString());
                    upsert.setString(2, personalizationSettings.getNameColor().name());
                    upsert.setString(3, personalizationSettings.getNickname());
                    upsert.setBoolean(4, personalizationSettings.isHideProfanity());
                    upsert.setBoolean(5, personalizationSettings.isAllowDmsFromAnyone());
                    upsert.setBoolean(6, personalizationSettings.isSilentJoinLeave());
                    upsert.setString(7, String.join(",", personalizationSettings.getMutedChannelDbNames()));
                    upsert.setString(8, personalizationSettings.getNameColor().name());
                    upsert.setString(9, personalizationSettings.getNickname());
                    upsert.setBoolean(10, personalizationSettings.isHideProfanity());
                    upsert.setBoolean(11, personalizationSettings.isAllowDmsFromAnyone());
                    upsert.setBoolean(12, personalizationSettings.isSilentJoinLeave());
                    upsert.setString(13, String.join(",", personalizationSettings.getMutedChannelDbNames()));
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<PersonalizationSettingsQuery> queryPersonalizationSettings(ChatPlayer chatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            PersonalizationSettingsQuery personalizationSettingsQuery = new PersonalizationSettingsQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.PERSONALIZATION_SETTINGS + " WHERE id = ?;")) {
                    query.setString(1, chatPlayer.getMojangId().toString());

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            ChatColor nameColor = ChatColor.valueOf(resultSet.getString("name_color"));
                            String nickname = resultSet.getString("nickname");
                            boolean hideProfanity = resultSet.getBoolean("hide_profanity");
                            boolean allowDmsFromAnyone = resultSet.getBoolean("allow_dms_from_anyone");
                            boolean isSilentJoinLeave = resultSet.getBoolean("silent_join_leave");
                            String mutedChannelDbNamesCombined = resultSet.getString("muted_channel_db_names");
                            List<String> mutedChannelDbNames;
                            if (mutedChannelDbNamesCombined != null) {
                                mutedChannelDbNames = Lists.newArrayList(mutedChannelDbNamesCombined.split(","));
                            } else {
                                mutedChannelDbNames = new ArrayList<>();
                            }
                            PersonalizationSettings personalizationSettings = new PersonalizationSettings(nameColor, nickname, hideProfanity, allowDmsFromAnyone, isSilentJoinLeave, mutedChannelDbNames);
                            personalizationSettingsQuery.addResult(personalizationSettings);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return personalizationSettingsQuery;
        });
    }

    public abstract Connection createConnection() throws SQLException;

}
