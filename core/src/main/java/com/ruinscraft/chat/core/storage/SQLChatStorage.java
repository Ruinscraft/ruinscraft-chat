package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

    @Override
    public void savePlayer(IChatPlayer player) {
        try {
            savePlayer(player, getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPlayer(IChatPlayer player) {
        try {
            loadPlayer(player, getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void logMessage(IChatMessage message) {
        try {
            logMessage(message, getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract Connection getConnection();

}
