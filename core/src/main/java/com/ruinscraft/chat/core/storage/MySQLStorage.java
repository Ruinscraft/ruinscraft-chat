package com.ruinscraft.chat.core.storage;

import com.ruinscraft.chat.core.Chat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLStorage extends GenericSQLStorage {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLStorage(Chat chat, String host, int port, String database, String username, String password) {
        super(chat);

        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection createConnection() throws SQLException {
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        return DriverManager.getConnection(jdbcUrl, username, password);
    }

}
