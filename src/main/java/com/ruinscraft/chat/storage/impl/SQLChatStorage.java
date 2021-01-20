package com.ruinscraft.chat.storage.impl;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.storage.ChatStorage;
import com.ruinscraft.chat.storage.query.*;

import java.sql.*;
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
    }

    protected void createTables() {
        try (Connection connection = createConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.CHAT_PLAYERS + " (id VARCHAR(36), username VARCHAR(16), first_seen BIGINT, last_seen BIGINT, focused VARCHAR(16), PRIMARY KEY (id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.ONLINE_CHAT_PLAYERS + " (id VARCHAR(36), updated_at BIGINT, server_name VARCHAR(32), group_name VARCHAR(32), vanished BOOL, PRIMARY KEY (id), FOREIGN KEY (id) REFERENCES " + Table.CHAT_PLAYERS + "(id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.CHAT_MESSAGES + " (id VARCHAR(36), server_id VARCHAR(36), channel VARCHAR(16), time BIGINT, sender_id VARCHAR(36), content VARCHAR(255), PRIMARY KEY (id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.FRIEND_REQUESTS + " (requester_id VARCHAR(36), target_id VARCHAR(36), time BIGINT, accepted BOOL, FOREIGN KEY (requester_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (target_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), UNIQUE KEY friend (requester_id, target_id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.MAIL_MESSAGES + " (id VARCHAR(36), sender_id VARCHAR(36), recipient_id VARCHAR(36), time BIGINT, is_read BOOL, content VARCHAR(255), PRIMARY KEY (id), FOREIGN KEY (sender_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (recipient_id) REFERENCES " + Table.CHAT_PLAYERS + "(id));");
                statement.addBatch("CREATE TABLE IF NOT EXISTS " + Table.BLOCKED_PLAYERS + "(blocker_id VARCHAR(36), blocked_id VARCHAR(36), FOREIGN KEY (blocker_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), FOREIGN KEY (blocked_id) REFERENCES " + Table.CHAT_PLAYERS + "(id), UNIQUE KEY block (blocker_id, blocked_id));");
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
                        "INSERT INTO " + Table.CHAT_PLAYERS + " (id, username, first_seen, last_seen, focused) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?, last_seen = ?, focused = ?;")) {
                    update.setString(1, chatPlayer.getMojangId().toString());
                    update.setString(2, chatPlayer.getMinecraftUsername());
                    update.setLong(3, chatPlayer.getFirstSeen());
                    update.setLong(4, chatPlayer.getLastSeen());
                    update.setString(5, chatPlayer.getFocused().getName());
                    update.setString(6, chatPlayer.getMinecraftUsername());
                    update.setLong(7, chatPlayer.getLastSeen());
                    update.setString(8, chatPlayer.getFocused().getName());
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
                            String focusedName = resultSet.getString("focused");
                            ChatChannel focused = chatPlugin.getChatChannelManager().getChannel(focusedName);
                            ChatPlayer chatPlayer = new ChatPlayer(mojangId, username, firstSeen, lastSeen, focused);
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
                    insert.setString(3, chatMessage.getChannel().getName());
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
                            String channelName = resultSet.getString("channel");
                            ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(channelName);
                            long time = resultSet.getLong("time");
                            UUID senderId = UUID.fromString(resultSet.getString("sender_id"));
                            String content = resultSet.getString("content");
                            ChatPlayer sender = chatPlugin.getChatPlayerManager().getOrLoad(senderId).join();
                            ChatMessage chatMessage = new ChatMessage(chatMessageId, serverId, channel, time, sender, content);
                            chatMessageQuery.addResult(chatMessage);
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
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO " + Table.ONLINE_CHAT_PLAYERS + " (id, updated_at, server_name, group_name, vanished) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE updated_at = ?, group_name = ?, vanished = ?;")) {
                    upsert.setString(1, chatPlayer.getMojangId().toString());
                    upsert.setLong(2, chatPlayer.getUpdatedAt());
                    upsert.setString(3, chatPlayer.getServerName());
                    upsert.setString(4, chatPlayer.getGroupName());
                    upsert.setBoolean(5, chatPlayer.isVanished());
                    upsert.setLong(6, chatPlayer.getUpdatedAt());
                    upsert.setString(7, chatPlayer.getGroupName());
                    upsert.setBoolean(8, chatPlayer.isVanished());
                    upsert.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
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
                            long updated = resultSet.getLong("updated_at");
                            String serverName = resultSet.getString("server_name");
                            String groupName = resultSet.getString("group_name");
                            boolean vanished = resultSet.getBoolean("vanished");
                            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getOrLoad(mojangId).join();
                            final OnlineChatPlayer onlineChatPlayer;

                            if (chatPlayer instanceof OnlineChatPlayer) {
                                onlineChatPlayer = (OnlineChatPlayer) chatPlayer;
                            } else {
                                onlineChatPlayer = new OnlineChatPlayer(chatPlayer, updated, serverName, groupName, vanished);
                            }

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
                    delete.setString(1, friendRequest.getRequester().toString());
                    delete.setString(2, friendRequest.getTarget().toString());
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
                            ChatPlayer requester = chatPlugin.getChatPlayerManager().getOrLoad(requesterId).join();
                            ChatPlayer target = chatPlugin.getChatPlayerManager().getOrLoad(targetId).join();
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
    public CompletableFuture<MailMessageQuery> queryMailMessages(OnlineChatPlayer onlineChatPlayer) {
        return CompletableFuture.supplyAsync(() -> {
            MailMessageQuery mailMessageQuery = new MailMessageQuery();

            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("SELECT * FROM " + Table.MAIL_MESSAGES + " WHERE recipient_id = ? AND is_read = ?;")) {
                    query.setString(1, onlineChatPlayer.getMojangId().toString());
                    query.setBoolean(2, false);

                    try (ResultSet resultSet = query.executeQuery()) {
                        while (resultSet.next()) {
                            UUID id = UUID.fromString(resultSet.getString("id"));
                            UUID senderId = UUID.fromString(resultSet.getString("sender_id"));
                            long time = resultSet.getLong("time");
                            boolean read = resultSet.getBoolean("is_read");
                            String content = resultSet.getString("content");
                            ChatPlayer sender = chatPlugin.getChatPlayerManager().getOrLoad(senderId).join();
                            MailMessage mailMessage = new MailMessage(id, sender, onlineChatPlayer, time, read, content);
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
                            ChatPlayer blockedChatPlayer = chatPlugin.getChatPlayerManager().getOrLoad(blockedId).join();
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
                try (PreparedStatement delete = connection.prepareStatement("DELETE FROM " + Table.BLOCKED_PLAYERS + " WHERE blocked_id = ? AND blocker_id = ?;")) {
                    delete.setString(1, blocker.getMojangId().toString());
                    delete.setString(2, blocked.getMojangId().toString());
                    delete.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract Connection createConnection() throws SQLException;

}
