package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.core.Chat;

import java.sql.*;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class SQLChatStorage implements IChatStorage {

    private Chat chat;

    public SQLChatStorage(Chat chat) {
        this.chat = chat;
    }

    public SQLChatStorage() {
        try (Statement statement = getConnection().createStatement()) {
            /*
             *  Create players table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_players (" +
                    "id INT AUTO_INCREMENT NOT NULL, " +
                    "mojang_uuid VARCHAR (36) NOT NULL, " +
                    "nickname VARCHAR (16) NOT NULL, " +
                    "channel VARCHAR (32) NOT NULL, " +
                    "UNIQUE (mojang_uuid), " +
                    "PRIMARY KEY (id)" +
                    ");");

            /*
             *  Create player blocked table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_player_blocked (" +
                    "blocker_id INT NOT NULL, " +
                    "blocked_id INT NOT NULL, " +
                    "UNIQUE (blocker_id, blocked_id)" +
                    ");");

            /*
             *  Create player muted table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_player_muted (" +
                    "player_id INT NOT NULL, " +
                    "channel VARCHAR (32) NOT NULL, " +
                    "UNIQUE (player_id, channel)" +
                    ");");

            /*
             *  Create player spying table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_player_spying (" +
                    "player_id INT NOT NULL, " +
                    "channel VARCHAR (32) NOT NULL, " +
                    "UNIQUE (player_id, channel)" +
                    ");");

            /*
             *  Create player status table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_player_status (" +
                    "player_id INT NOT NULL, " +
                    "node_id VARCHAR (36) NOT NULL, " +
                    "last_seen TIMESTAMP, " +
                    "UNIQUE (player_id, node_id)" +
                    ");");

            /*
             *  Create logs table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_logs (" +
                    "timestamp TIMESTAMP, " +
                    "format VARCHAR (255), " +
                    "sender_name VARCHAR (16), " +
                    "recipient_name VARCHAR (16), " +
                    "content VARCHAR (255)" +
                    ");");

            /*
             *  Execute batch
             */
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlayer(IChatPlayer player, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement(
                "INSERT INTO chat_players (mojang_uuid, nickname) VALUES (?, ?) ON DUPLICATE KEY UPDATE nickname = ?;", Statement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, player.getMojangId().toString());
            insert.setString(2, player.getNickname());
            insert.setString(3, player.getNickname());

            insert.execute();

            try (ResultSet result = insert.getGeneratedKeys()) {
                if (result.next()) {
                    player.setId(result.getInt(1));
                }
            }
        }
    }

    private void loadPlayer(IChatPlayer player, Connection connection) throws SQLException {
        /*
         *  Query players table
         */
        try (PreparedStatement query = connection.prepareStatement(
                "SELECT * FROM chat_players where mojang_uuid = ? OR id = ?;")) {
            query.setString(1, player.getMojangId().toString());
            query.setInt(2, player.getId());

            try (ResultSet result = query.executeQuery()) {
                while (result.next()) {
                    player.setId(result.getInt("id"));
                    player.setNickname(result.getString("nickname"));

                    String channelName = result.getString("channel");
                    IChatChannel channel = chat.getChannel(channelName);

                    if (channel != null) {
                        player.setFocused(channel);
                    }
                }
            }
        }

        /*
         *  Query player blocked table
         */

        /*
         *  Query player muted table
         */

        /*
         *  Query player spying table
         */
    }

    private void logMessage(IChatMessage message, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement("")) {

        }
    }

    private void setOnlinePlayers(Set<IChatPlayer> online, Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("")) {

        }
    }

    private void purgeOfflinePlayers(Set<IChatPlayer> toPurge, Connection connection) throws SQLException {
        try (PreparedStatement delete = connection.prepareStatement("")) {

        }
    }

    @Override
    public CompletableFuture<Void> savePlayer(IChatPlayer player) {
        return CompletableFuture.runAsync(() -> {
            try {
                savePlayer(player, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> loadPlayer(IChatPlayer player) {
        return CompletableFuture.runAsync(() -> {
            try {
                loadPlayer(player, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> setOnlinePlayers(Set<IChatPlayer> online) {
        return CompletableFuture.runAsync(() -> {
            try {
                setOnlinePlayers(online, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> purgeOfflinePlayers(Set<IChatPlayer> toPurge) {
        return CompletableFuture.runAsync(() -> {
            try {
                purgeOfflinePlayers(toPurge, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> logMessage(IChatMessage message) {
        return CompletableFuture.runAsync(() -> {
            try {
                logMessage(message, getConnection());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public abstract Connection getConnection();

}
