package com.ruinscraft.chat;

import com.ruinscraft.chat.listener.ChatListener;
import com.ruinscraft.chat.pubsub.PubSub;
import com.ruinscraft.chat.pubsub.RedisPubSub;
import com.ruinscraft.chat.storage.Storage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ChatPlugin extends JavaPlugin {

    private PubSub pubSub;
    private Storage storage;

    public PubSub getPubSub() {
        return pubSub;
    }

    public Storage getStorage() {
        return storage;
    }

    public ChatPlayer getChatPlayer(UUID mojangId) {
        return new ChatPlayer(mojangId);
    }

    public ChatChannel getChatChannel(String channelName) {
        return new ChatChannel("default");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // setup pubsub
        if (getConfig().getString("pubsub.type").equals("redis")) {
            String redisHost = getConfig().getString("pubsub.redis.host");
            int redisPort = getConfig().getInt("pubsub.redis.port");

            pubSub = new RedisPubSub(redisHost, redisPort, this);

            getLogger().info("Using Redis for PubSub");
        }

        // setup storage
        if (getConfig().getString("storage.type").equals("mysql")) {
            String mysqlHost;
            int mysqlPort;
            String mysqlDatabase;
            String mysqlUsername;
            String mysqlPassword;

            getLogger().info("Using MySQL for Storage");
        }

        // register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
    }

    @Override
    public void onDisable() {
        pubSub.close();
    }

}
