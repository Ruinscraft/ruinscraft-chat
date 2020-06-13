package com.ruinscraft.chat.core.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLChatStorage extends SQLChatStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
    private Connection connection;

    public MySQLChatStorage(String host, int port, String database, String username, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        createTables();
    }

    @Override
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;
                connection = DriverManager.getConnection(jdbcUrl, username, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
