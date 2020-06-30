package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.channel.DefaultChatChannel;
import com.ruinscraft.chat.core.messagebroker.RedisMessageBroker;
import com.ruinscraft.chat.core.player.OnlinePlayers;
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
    private IMessageBroker messageBroker;
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
    public IMessageBroker getMessageBroker() {
        return messageBroker;
    }

    @Override
    public IOnlinePlayers getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public IChatPlayer getChatPlayer(UUID playerId) {
        return onlinePlayers.get(playerId);
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
    public IChatChannel getChannel(String name) {
        return getChannels().get(name);
    }

    @Override
    public IChatChannel getDefaultChannel() {
        return channels.get("default");
    }

    public PlayerHeartbeatTask getHeartbeatTask() {
        return heartbeatTask;
    }

    @Override
    public void start() throws Exception {
        nodeId = UUID.randomUUID();

        // load config
        platform.getJLogger().info("Loading configuration");
        config = new ChatConfig();
        platform.loadConfigFromDisk(config);

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

        // load message broker
        platform.getJLogger().info("Loading message broker");
        if (config.messageBrokerType.equals("redis")) {
            String host = config.messageBrokerRedisHost;
            int port = config.messageBrokerRedisPort;

            messageBroker = new RedisMessageBroker(host, port);
        } else {
            throw new Exception("Could not set up message broker. No valid message broker type defined.");
        }

        // setup chat channels
        platform.getJLogger().info("Loading channels");
        channels = new HashMap<>();
        channels.put("default", new DefaultChatChannel(platform));

        // setup chat loggers
        platform.getJLogger().info("Loading loggers");
        loggers = new HashMap<>();

        // setup chat filters
        platform.getJLogger().info("Loading filters");
        filters = new HashMap<>();

        // create online players
        onlinePlayers = new OnlinePlayers();

        // start player heartbeat task
        heartbeatTask = new PlayerHeartbeatTask(platform);
        platform.runTaskTimerAsync(heartbeatTask, 0L, 20L);
    }

    @Override
    public void shutdown() throws Exception {
        storage.close();

        nodeId = null;
    }

}
