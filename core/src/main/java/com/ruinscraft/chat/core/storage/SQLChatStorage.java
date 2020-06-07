package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class SQLChatStorage implements IChatStorage {

    public SQLChatStorage() {
        try (Statement statement = getConnection().createStatement()) {
            /*
             *  Create players table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_players (" +
                    "id INT AUTO_INCREMENT NOT NULL, " +
                    "mojang_uuid VARCHAR (36) NOT NULL, " +
                    "nickname VARCHAR (16) NOT NULL, " +
                    "UNIQUE (mojang_uuid), " +
                    "PRIMARY KEY (id)" +
                    ");");

            /*
             *  Create player channels table
             */
            statement.addBatch("CREATE TABLE IF NOT EXISTS chat_player_channels (" +
                    "player_id INT NOT NULL, " +
                    "channel VARCHAR (32) NOT NULL, " +
                    "UNIQUE (player_id, channel)" +
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
             *  Create status table
             */
            statement.addBatch("");

            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void savePlayer(IChatPlayer player, Connection connection) throws SQLException {
        try (PreparedStatement insert = connection.prepareStatement("")) {

        }
    }

    private void loadPlayer(IChatPlayer player, Connection connection) throws SQLException {
        try (PreparedStatement query = connection.prepareStatement("")) {

        }
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
