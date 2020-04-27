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
            storage = new MySQLChatStorage(true,
                    config.storageMySQLHost,
                    config.storageMySQLPort,
                    config.storageMySQLDatabase,
                    config.storageMySQLUsername,
                    config.storageMySQLPassword);
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
        loggers.clear();
        channels.clear();
        filters.clear();


    }

}
