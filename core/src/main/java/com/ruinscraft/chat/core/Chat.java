package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.storage.MySQLChatStorage;

import java.util.HashMap;
import java.util.Map;

public class Chat implements IChat {

    private ChatConfig config;
    private IChatStorage storage;

    private Map<String, IChatChannel> channels;
    private Map<String, IChatLogger> loggers;
    private Map<String, IMessageFilter> filters;

    public ChatConfig getConfig() {
        if (config == null) {
            config = new ChatConfig();
        }

        return config;
    }

    @Override
    public IChatStorage getStorage() {
        return storage;
    }

    @Override
    public Map<String, IChatLogger> getLoggers() {
        return loggers;
    }

    @Override
    public Map<String, IChatChannel> getChannels() {
        return channels;
    }

    @Override
    public Map<String, IMessageFilter> getFilters() {
        return filters;
    }

    @Override
    public void start() {
        if (config.storageType.equals("mysql")) {
            String host = config.storageMySQLHost;
            int port = config.storageMySQLPort;
            String db = config.storageMySQLDatabase;
            String user = config.storageMySQLUsername;
            String pass = config.storageMySQLPassword;

            storage = new MySQLChatStorage(host, port, db, user, pass);
        }

        if (storage == null) {
            throw new RuntimeException("No storage defined");
        }

        loggers = new HashMap<>();
        channels = new HashMap<>();
        filters = new HashMap<>();


    }

    @Override
    public void shutdown() {
        storage.close();
    }

}
