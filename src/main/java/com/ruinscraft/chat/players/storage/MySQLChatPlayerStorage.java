package com.ruinscraft.chat.players.storage;

import com.google.common.collect.Sets;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;
import com.ruinscraft.chat.players.MinecraftIdentity;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class MySQLChatPlayerStorage implements SQLChatPlayerStorage {

    /* PLAYER TABLE */
    private static final String SQL_CREATE_PLAYERS = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT AUTO_INCREMENT, mojang_uuid VARCHAR(36), nickname VARCHAR(24), focused VARCHAR(16), PRIMARY KEY (chat_player_id), UNIQUE (mojang_uuid));", Table.PLAYERS);
    private static final String SQL_SELECT_PLAYER_BY_UUID = String.format("SELECT * FROM %s WHERE mojang_uuid = ?;", Table.PLAYERS);
    private static final String SQL_UPDATE_PLAYERS = String.format("UPDATE %s SET mojang_uuid = ?, nickname = ?, focused = ? WHERE chat_player_id = ?;", Table.PLAYERS);
    private static final String SQL_INSERT_PLAYERS = String.format("INSERT INTO %s (mojang_uuid, nickname, focused) VALUES (?, ?, ?);", Table.PLAYERS);

    /* IGNORING TABLE */
    private static final String SQL_CREATE_IGNORING = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, minecraft_identity VARCHAR(36), UNIQUE KEY (chat_player_id, minecraft_identity), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.IGNORING, Table.PLAYERS);
    private static final String SQL_SELECT_IGNORING = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.IGNORING);
    private static final String SQL_INSERT_IGNORING = String.format("INSERT INTO %s (chat_player_id, minecraft_identity) VALUES (?, ?);", Table.IGNORING);
    private static final String SQL_DELETE_IGNORING = String.format("DELETE FROM %s WHERE chat_player_id = ? AND minecraft_identity = ?;", Table.IGNORING);

    /* MUTES TABLE */
    private static final String SQL_CREATE_MUTED = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, channel_name VARCHAR(16), UNIQUE KEY (chat_player_id, channel_name), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.MUTED, Table.PLAYERS);
    private static final String SQL_SELECT_MUTED = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.MUTED);
    private static final String SQL_INSERT_MUTED = String.format("INSERT INTO %s (chat_player_id, channel_name) VALUES (?, ?);", Table.MUTED);
    private static final String SQL_DELETE_MUTED = String.format("DELETE FROM %s WHERE chat_player_id = ? AND channel_name = ?;", Table.MUTED);

    /* SPYING TABLE */
    private static final String SQL_CREATE_SPYING = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, channel_name VARCHAR(16), UNIQUE KEY (chat_player_id, channel_name), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.SPYING, Table.PLAYERS);
    private static final String SQL_SELECT_SPYING = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.SPYING);
    private static final String SQL_INSERT_SPYING = String.format("INSERT INTO %s (chat_player_id, channel_name) VALUES (?, ?);", Table.SPYING);
    private static final String SQL_DELETE_SPYING = String.format("DELETE FROM %s WHERE chat_player_id = ? AND channel_name = ?;", Table.SPYING);

    /* META TABLE */
    private static final String SQL_CREATE_META = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, meta_key VARCHAR(32), meta_value VARCHAR(256), UNIQUE KEY (chat_player_id, meta_key), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.META, Table.PLAYERS);
    private static final String SQL_SELECT_META = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.META);
    private static final String SQL_INSERT_META = String.format("INSERT INTO %s (chat_player_id, meta_key, meta_value) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE meta_value = ?;", Table.META);

    /* Connection pool */
    private HikariDataSource dataSource;

    public MySQLChatPlayerStorage(String address, int port, String database, String username, char[] password) {
        dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", address, port, database));
        dataSource.setUsername(username);
        dataSource.setPassword(new String(password));
        dataSource.setPoolName("ruinscraft-chat-player-storage-pool");
        dataSource.setMaximumPoolSize(5);
        dataSource.setConnectionTimeout(3000);
        dataSource.setLeakDetectionThreshold(3000);

        try (Connection connection = getConnection()) {
            if (connection.isClosed()) {
                ChatPlugin.warning("Player storage MySQL connection lost");
                return;
            } else {
                ChatPlugin.info("Player storage MySQL connection established");
            }

            /* CREATE TABLES */
            try (PreparedStatement create_players = connection.prepareStatement(SQL_CREATE_PLAYERS);
                 PreparedStatement create_ignoring = connection.prepareStatement(SQL_CREATE_IGNORING);
                 PreparedStatement create_muted = connection.prepareStatement(SQL_CREATE_MUTED);
                 PreparedStatement create_spying = connection.prepareStatement(SQL_CREATE_SPYING);
                 PreparedStatement create_meta = connection.prepareStatement(SQL_CREATE_META)) {
                create_players.execute();
                create_ignoring.execute();
                create_muted.execute();
                create_spying.execute();
                create_meta.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Callable<Void> loadChatPlayer(ChatPlayer chatPlayer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection connection = getConnection()) {
                    /* SELECT FROM PLAYERS TABLE */
                    try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_PLAYER_BY_UUID)) {
                        select.setString(1, chatPlayer.getMojangUUID().toString());

                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                int chatPlayerId = rs.getInt("chat_player_id");
                                String nickname = rs.getString("nickname");
                                String focused = rs.getString("focused");

                                chatPlayer.setChatPlayerId(chatPlayerId);
                                chatPlayer.setNickname(nickname);
                                chatPlayer.setFocused(focused);
                            }
                        }
                    }

                    if (chatPlayer.getChatPlayerId() == 0) {
                        return null;
                    }

                    /* SELECT FROM IGNORING TABLE */
                    try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_IGNORING)) {
                        select.setInt(1, chatPlayer.getChatPlayerId());

                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                String identityString = rs.getString("minecraft_identity");
                                MinecraftIdentity minecraftIdentity = new MinecraftIdentity(identityString);

                                chatPlayer.ignoring.add(minecraftIdentity);
                            }
                        }
                    }

                    /* SELECT FROM MUTED TABLE */
                    try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_MUTED)) {
                        select.setInt(1, chatPlayer.getChatPlayerId());

                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                String channelName = rs.getString("channel_name");
                                ChatChannel<? extends ChatMessage> chatChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(channelName);

                                chatPlayer.muted.add(chatChannel);
                            }
                        }
                    }

                    /* SELECT FROM SPYING TABLE */
                    try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_SPYING)) {
                        select.setInt(1, chatPlayer.getChatPlayerId());

                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                String channelName = rs.getString("channel_name");
                                ChatChannel<? extends ChatMessage> chatChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(channelName);

                                chatPlayer.spying.add(chatChannel);
                            }
                        }
                    }

                    /* SELECT FROM META TABLE */
                    try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_META)) {
                        select.setInt(1, chatPlayer.getChatPlayerId());

                        try (ResultSet rs = select.executeQuery()) {
                            while (rs.next()) {
                                String key = rs.getString("meta_key");
                                String value = rs.getString("meta_value");

                                chatPlayer.meta.put(key, value);
                            }
                        }
                    }
                }

                return null;
            }
        };
    }

    @Override
    public Callable<Void> saveChatPlayer(ChatPlayer chatPlayer) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (Connection connection = getConnection()) {
                    /* Player not in database, insert */
                    if (chatPlayer.getChatPlayerId() == 0) {
                        try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_PLAYERS, Statement.RETURN_GENERATED_KEYS)) {
                            insert.setString(1, chatPlayer.getMojangUUID().toString());
                            insert.setString(2, chatPlayer.getNickname());
                            insert.setString(3, chatPlayer.getFocused().getName());
                            insert.execute();

                            try (ResultSet rs = insert.getGeneratedKeys()) {
                                while (rs.next()) {
                                    int chatPlayerId = rs.getInt(1);
                                    chatPlayer.setChatPlayerId(chatPlayerId);
                                }
                            }
                        }
                    }

                    /* Player already in database, update */
                    else {
                        /* UPDATE PLAYERS TABLE */
                        try (PreparedStatement update = connection.prepareStatement(SQL_UPDATE_PLAYERS)) {
                            update.setString(1, chatPlayer.getMojangUUID().toString());
                            update.setString(2, chatPlayer.getNickname());
                            update.setString(3, chatPlayer.getFocused().getName());
                            update.setInt(4, chatPlayer.getChatPlayerId());

                            update.execute();
                        }

                        /* UPDATE IGNORING TABLE */
                        Set<String> currentIgnoring = chatPlayer.ignoring.stream().map(MinecraftIdentity::getIdentity).collect(Collectors.toSet());
                        Set<String> previousIgnoring = new HashSet<>();

                        try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_IGNORING)) {
                            select.setInt(1, chatPlayer.getChatPlayerId());

                            try (ResultSet rs = select.executeQuery()) {
                                while (rs.next()) {
                                    String raw = rs.getString("minecraft_identity");
                                    previousIgnoring.add(raw);
                                }
                            }
                        }

                        for (String inserting : Sets.difference(currentIgnoring, previousIgnoring)) {
                            try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_IGNORING)) {
                                insert.setInt(1, chatPlayer.getChatPlayerId());
                                insert.setString(2, inserting);
                                insert.execute();
                            }
                        }

                        for (String deleting : Sets.difference(previousIgnoring, currentIgnoring)) {
                            try (PreparedStatement delete = connection.prepareStatement(SQL_DELETE_IGNORING)) {
                                delete.setInt(1, chatPlayer.getChatPlayerId());
                                delete.setString(2, deleting);
                                delete.execute();
                            }
                        }

                        /* UPDATE MUTED TABLE */
                        Set<ChatChannel<?>> currentMuted = chatPlayer.muted;
                        Set<ChatChannel<?>> previousMuted = new HashSet<>();

                        try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_MUTED)) {
                            select.setInt(1, chatPlayer.getChatPlayerId());

                            try (ResultSet rs = select.executeQuery()) {
                                while (rs.next()) {
                                    String raw = rs.getString("channel_name");
                                    previousMuted.add(ChatPlugin.getInstance().getChatChannelManager().getByName(raw));
                                }
                            }
                        }

                        for (ChatChannel<?> inserting : Sets.difference(currentMuted, previousMuted)) {
                            try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_MUTED)) {
                                insert.setInt(1, chatPlayer.getChatPlayerId());
                                insert.setString(2, inserting.getName());
                                insert.execute();
                            }
                        }

                        for (ChatChannel<?> deleting : Sets.difference(previousMuted, currentMuted)) {
                            try (PreparedStatement delete = connection.prepareStatement(SQL_DELETE_MUTED)) {
                                delete.setInt(1, chatPlayer.getChatPlayerId());
                                delete.setString(2, deleting.getName());
                                delete.execute();
                            }
                        }

                        /* UPDATE SPYING TABLE */
                        Set<ChatChannel<?>> currentSpying = chatPlayer.spying;
                        Set<ChatChannel<?>> previousSpying = new HashSet<>();

                        try (PreparedStatement select = connection.prepareStatement(SQL_SELECT_SPYING)) {
                            select.setInt(1, chatPlayer.getChatPlayerId());

                            try (ResultSet rs = select.executeQuery()) {
                                while (rs.next()) {
                                    String raw = rs.getString("channel_name");
                                    previousSpying.add(ChatPlugin.getInstance().getChatChannelManager().getByName(raw));
                                }
                            }
                        }

                        for (ChatChannel<?> inserting : Sets.difference(currentSpying, previousSpying)) {
                            try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_SPYING)) {
                                insert.setInt(1, chatPlayer.getChatPlayerId());
                                insert.setString(2, inserting.getName());
                                insert.execute();
                            }
                        }

                        for (ChatChannel<?> deleting : Sets.difference(previousSpying, currentSpying)) {
                            try (PreparedStatement delete = connection.prepareStatement(SQL_DELETE_SPYING)) {
                                delete.setInt(1, chatPlayer.getChatPlayerId());
                                delete.setString(2, deleting.getName());
                                delete.execute();
                            }
                        }

                        /* UPDATE META TABLE */
                        for (Map.Entry<String, String> metaEntry : chatPlayer.meta.entrySet()) {
                            String key = metaEntry.getKey();
                            String value = metaEntry.getValue();

                            try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_META)) {
                                insert.setInt(1, chatPlayer.getChatPlayerId());
                                insert.setString(2, key);
                                insert.setString(3, value);
                                insert.setString(4, value);
                                insert.execute();
                            }
                        }
                    }
                }

                return null;
            }
        };
    }

    @Override
    public void close() {
        dataSource.close();
    }

    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
