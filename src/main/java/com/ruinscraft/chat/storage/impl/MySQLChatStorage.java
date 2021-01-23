package com.ruinscraft.chat.storage.impl;

import com.ruinscraft.chat.ChatPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLChatStorage extends SQLChatStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    private HikariDataSource ds;

    public MySQLChatStorage(ChatPlugin chatPlugin, String host, int port, String database, String username, String password) {
        super(chatPlugin);
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;

        HikariConfig config = new HikariConfig();
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        config.setPoolName("ruinscraft-chat-pool");

        ds = new HikariDataSource(config);

        createTables();
    }

    @Override
    public Connection createConnection() throws SQLException {
//        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false";
//        return DriverManager.getConnection(jdbcUrl, username, password);
        return ds.getConnection();
    }

}
