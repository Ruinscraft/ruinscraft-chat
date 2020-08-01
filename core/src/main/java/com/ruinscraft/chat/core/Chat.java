package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.*;
import com.ruinscraft.chat.core.channel.ChatChannel;
import com.ruinscraft.chat.core.channel.ChatChannelManager;
import com.ruinscraft.chat.core.message.ChatMessageManager;
import com.ruinscraft.chat.core.message.RedisChatMessageManager;
import com.ruinscraft.chat.core.player.ChatPlayerManager;
import com.ruinscraft.chat.core.player.PlayerStatusManager;
import com.ruinscraft.chat.core.storage.MySQLStorage;

import java.util.Set;

public class Chat implements IChat {

    private ChatPlatform platform;

    private ChatConfig config;
    private IChatStorage storage;
    private IPlayerStatusManager playerStatusManager;
    private ChatPlayerManager playerManager;
    private ChatChannelManager channelManager;
    private ChatMessageManager messageManager;

    public Chat(ChatPlatform platform, ChatChannel _default, Set<ChatChannel> channels) {
        this.platform = platform;
        config = platform.loadConfig();

        setupStorage();

        playerStatusManager = new PlayerStatusManager(this);
        playerManager = new ChatPlayerManager(this);
        channelManager = new ChatChannelManager(_default, channels);

        setupMessageManager();
    }

    public ChatPlatform getPlatform() {
        return platform;
    }

    @Override
    public ChatConfig getConfig() {
        return config;
    }

    @Override
    public IChatStorage getStorage() {
        return storage;
    }

    @Override
    public IChatMessageManager getMessageManager() {
        return messageManager;
    }

    @Override
    public IPlayerStatusManager getPlayerStatusManager() {
        return playerStatusManager;
    }

    @Override
    public void run(Runnable runnable) {
        platform.run(runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        platform.runAsync(runnable);
    }

    public ChatPlayerManager getPlayerManager() {
        return playerManager;
    }

    public ChatChannelManager getChannelManager() {
        return channelManager;
    }

    private void setupStorage() {
        String host = config.mysqlHost;
        int port = config.mysqlPort;
        String database = config.mysqlDatabase;
        String username = config.mysqlUsername;
        String password = config.mysqlPassword;

        storage = new MySQLStorage(this, host, port, database, username, password);
    }

    private void setupMessageManager() {
        String host = config.redisHost;
        int port = config.redisPort;

        messageManager = new RedisChatMessageManager(this, host, port);
    }

}
