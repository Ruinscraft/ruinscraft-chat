package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.storage.MySQLChatStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chat implements IChat {

    private UUID nodeId;

    private ChatPlatform platform;

    private ChatConfig config;
    private IChatStorage storage;

    private Map<String, IChatChannel> channels;
    private Map<String, IChatLogger> loggers;
    private Map<String, IMessageFilter> filters;

    public Chat(ChatPlatform platform) {
        this.platform = platform;
    }

    public ChatPlatform getPlatform() {
        return platform;
    }

    public ChatConfig getConfig() {
        return config;
    }

    @Override
    public UUID getNodeId() {
        return nodeId;
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
    public Map<String, IChatPlayer> getPlayers() {
        return null;
    }

    @Override
    public void start() throws Exception {
        nodeId = UUID.randomUUID();

        // load config
        platform.getJLogger().info("Loading configuration");
        config = platform.loadConfigFromDisk();

        // load storage
        platform.getJLogger().info("Loading storage");
        if (config.storageType.equals("mysql")) {
            String host = config.storageMySQLHost;
            int port = config.storageMySQLPort;
            String db = config.storageMySQLDatabase;
            String user = config.storageMySQLUsername;
            String pass = config.storageMySQLPassword;
            storage = new MySQLChatStorage(host, port, db, user, pass);
        } else {
            throw new Exception("Could not setup storage. No valid storage type defined.");
        }

        // setup chat channels
        platform.getJLogger().info("Loading channels");
        channels = new HashMap<>();

        // setup chat loggers
        platform.getJLogger().info("Loading loggers");
        loggers = new HashMap<>();

        // setup chat filters
        platform.getJLogger().info("Loading filters");
        filters = new HashMap<>();
    }

    @Override
    public void shutdown() throws Exception {
        storage.close();
    }

}
