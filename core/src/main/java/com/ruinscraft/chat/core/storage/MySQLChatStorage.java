package com.ruinscraft.chat.core.storage;

import java.sql.Connection;

public class MySQLChatStorage extends SQLChatStorage {

    private boolean pool;
    private String host;
    private int port;
    private String database;
    private String username;
    private String password;

    public MySQLChatStorage(boolean pool, String host, int port, String database, String username, String password) {
        this.pool = pool;
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
    }

    @Override
    public Connection getConnection() {
        return null; // TODO:
    }

    @Override
    public void close() {
        // TODO:
    }

}
