package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatMessageLog;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.api.IPlayerStatus;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.player.PlayerStatus;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class GenericSQLStorage implements IChatStorage {

    public GenericSQLStorage(Chat chat) {
        chat.run(() -> createTables());
    }

    private void createTables() {
        try (Connection connection = createConnection()) {
            try (Statement statement = connection.createStatement()) {
                // Create Players table
                statement.execute("CREATE TABLE IF NOT EXISTS chat_players (" +
                        "id INT AUTO_INCREMENT NOT NULL, " +
                        "mojang_uuid VARCHAR(36) NOT NULL, " +
                        "username VARCHAR(16), " +
                        "nickname VARCHAR(32), " +
                        "first_seen BIGINT, " +
                        "last_seen BIGINT, " +
                        "UNIQUE (mojang_uuid), " +
                        "PRIMARY KEY (id));");
                // Create Player Channels table
                statement.execute("CREATE TABLE IF NOT EXISTS chat_player_channels (" +
                        "player_id INT NOT NULL, " +
                        "server VARCHAR(32) NOT NULL, " +
                        "channel VARCHAR(32) NOT NULL, " +
                        "UNIQUE (player_id, server));");
                // Create Player Statuses table
                statement.execute("CREATE TABLE IF NOT EXISTS player_statuses (" +
                        "username VARCHAR(16) NOT NULL, " +
                        "gamemode VARCHAR(32) NOT NULL, " +
                        "updated_at BIGINT NOT NULL, " +
                        "UNIQUE (username, gamemode));");
//                // Create Player Parties table
//                statement.execute("CREATE TABLE IF NOT EXISTS chat_parties (" +
//                        ");");
//                // Create Player Friends table
//                statement.execute("CREATE TABLE IF NOT EXISTS chat_friends (" +
//                        ");");
                // Create Logs table
                statement.execute("CREATE TABLE IF NOT EXISTS chat_logs (" +
                        "sender VARCHAR(16), " +
                        "recipients TEXT, " +
                        "channel_type VARCHAR(32), " +
                        "time BIGINT " +
                        ");");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CompletableFuture<Void> log(IChatMessageLog chatMessageLog) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> loadPlayer(IChatPlayer chatPlayer) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement query = connection.prepareStatement("")) {

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Void> updateStatuses(String gamemode, Set<IChatPlayer> chatPlayers) {
        return CompletableFuture.runAsync(() -> {
            try (Connection connection = createConnection()) {
                try (PreparedStatement upsert = connection.prepareStatement("INSERT INTO player_statuses (username, gamemode, updated_at) VALUES (?, ?, ?) ON DUPLICATE KEY SET updated_at = ?;")) {
                    for (IChatPlayer chatPlayer : chatPlayers) {
                        upsert.setString(1, chatPlayer.getUsername());
                        upsert.setString(2, gamemode);
                        upsert.setLong(3, System.currentTimeMillis());
                        upsert.addBatch();
                    }

                    upsert.executeBatch();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CompletableFuture<Map<String, Set<IPlayerStatus>>> queryStatuses() {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Set<IPlayerStatus>> statuses = new HashMap<>();

            try (Connection connection = createConnection()) {
                try (Statement statement = connection.createStatement()) {
                    try (ResultSet result = statement.executeQuery("SELECT * FROM player_statuses;")) {
                        while (result.next()) {
                            String username = result.getString("username");
                            String gamemode = result.getString("gamemode");
                            long updatedAt = result.getLong("updated_at");

                            if (!statuses.containsKey(gamemode)) {
                                statuses.put(gamemode, new HashSet<>());
                            }

                            IPlayerStatus status = new PlayerStatus(username, gamemode, updatedAt);

                            statuses.get(gamemode).add(status);
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return statuses;
        });
    }

    public abstract Connection createConnection() throws SQLException;

}
