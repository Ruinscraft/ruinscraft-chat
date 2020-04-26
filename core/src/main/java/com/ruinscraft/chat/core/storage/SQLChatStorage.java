package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatStorage;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLChatStorage implements IChatStorage {

    @Override
    public void savePlayer(IChatPlayer player) {
        try (Connection connection = getConnection()) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadPlayer(IChatPlayer player) {
        try (Connection connection = getConnection()) {

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // close when finished
    public abstract Connection getConnection();

}
