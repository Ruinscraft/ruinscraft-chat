package com.ruinscraft.chat.storage.impl;

import com.ruinscraft.chat.ChatPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLChatStorage extends SQLChatStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLChatStorage(ChatPlugin chatPlugin, String host, int port, String database, String username, String password) {
        super(chatPlugin);
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        createTables();
    }

    @Override
    public Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

}
