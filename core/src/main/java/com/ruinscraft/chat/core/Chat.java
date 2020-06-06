package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.storage.MySQLChatStorage;
import com.ruinscraft.chat.core.tasks.PlayerHeartbeatTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Chat implements IChat {

    /**
     * Every Chat instance (ie a server running Chat) has a unique node id generated
     * at boot time which lasts until the node is destroyed
     * <p>
     * A null node id implies the Chat instance has been stopped or has not been started
     */
    private UUID nodeId;

    private ChatPlatform platform;

    private ChatConfig config;
    private IChatStorage storage;
    private IOnlinePlayers onlinePlayers;

    private Map<String, IChatChannel> channels;
    private Map<String, IChatLogger> loggers;
    private Map<String, IMessageFilter> filters;

    private PlayerHeartbeatTask heartbeatTask;

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
    public IOnlinePlayers getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public IChatPlayer getChatPlayer(UUID playerId) {
        return onlinePlayers.find(playerId).get(); // may be null
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

    public PlayerHeartbeatTask getHeartbeatTask() {
        return heartbeatTask;
    }

    @Override
    public void start() throws Exception {
        nodeId = UUID.randomUUID();

        // load config
        platform.getLogger().info("Loading configuration");
        config = new ChatConfig();
        platform.loadConfigFromDisk(config);

        // load storage
        platform.getLogger().info("Loading storage");
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
        platform.getLogger().info("Loading channels");
        channels = new HashMap<>();

        // setup chat loggers
        platform.getLogger().info("Loading loggers");
        loggers = new HashMap<>();

        // setup chat filters
        platform.getLogger().info("Loading filters");
        filters = new HashMap<>();

        // start player heartbeat task
        heartbeatTask = new PlayerHeartbeatTask(platform);
        platform.runTaskTimerAsync(heartbeatTask, 0L, 1000L); // period of 1 second
    }

    @Override
    public void shutdown() throws Exception {
        storage.close();

        nodeId = null;
    }

}
