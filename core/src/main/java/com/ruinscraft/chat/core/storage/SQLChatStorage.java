package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.api.IOnlinePlayers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class SQLChatStorage implements IChatStorage {

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

    private void queryOnlinePlayers(IOnlinePlayers onlinePlayers, Connection connection) throws SQLException {
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
    public CompletableFuture<Void> queryOnlinePlayers(IOnlinePlayers onlinePlayers) {
        return CompletableFuture.runAsync(() -> {
            try {
                queryOnlinePlayers(onlinePlayers, getConnection());
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
