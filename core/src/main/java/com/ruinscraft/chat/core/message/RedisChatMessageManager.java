package com.ruinscraft.chat.core.message;

import com.google.gson.Gson;
import com.ruinscraft.chat.api.IChatMessageLog;
import com.ruinscraft.chat.core.Chat;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public class RedisChatMessageManager extends ChatMessageManager {

    private static final String REDIS_CHANNEL = "ruinscraft-chat";
    private static final Gson GSON = new Gson();

    private JedisPool jedisPool;
    private RedisSubscriber subscriber;

    public RedisChatMessageManager(Chat chat, String host, int port) {
        super(chat);

        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(64);
        poolConfig.setMaxIdle(32);

        jedisPool = new JedisPool(poolConfig, host, port);

        CompletableFuture.runAsync(() -> {
            chat.getPlatform().log("Subscribing to Redis channel");

            subscriber = new RedisSubscriber();

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(subscriber, REDIS_CHANNEL);
            }
        });
    }

    @Override
    public void publish(IChatMessageLog log) {
        try {
            String json = GSON.toJson(log, ChatMessageLog.class);

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(REDIS_CHANNEL, json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }

        if (subscriber != null && subscriber.isSubscribed()) {
            subscriber.unsubscribe();
        }
    }

    private class RedisSubscriber extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(REDIS_CHANNEL)) {
                return;
            }

            try {
                ChatMessageLog log = GSON.fromJson(message, ChatMessageLog.class);

                consume(log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
