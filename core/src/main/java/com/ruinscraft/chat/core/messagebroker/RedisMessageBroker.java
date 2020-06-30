package com.ruinscraft.chat.core.messagebroker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ruinscraft.chat.api.*;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public class RedisMessageBroker implements IMessageBroker {

    private static final Gson GSON = new Gson();
    private static final String CHANNEL = "ruinscraft-chat";

    private final String host;
    private final int port;

    private JedisPool jedisPool;
    private Jedis subscriber;

    public RedisMessageBroker(String host, int port) {
        this.host = host;
        this.port = port;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(32);
        jedisPoolConfig.setMaxTotal(64);

        jedisPool = new JedisPool(jedisPoolConfig, host, port);

        CompletableFuture.runAsync(() -> {
            subscriber.subscribe(new RedisSubscriber(RedisMessageBroker.this), CHANNEL);
        });
    }

    private CompletableFuture<Void> pushMessage(JsonObject json) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(CHANNEL, json.getAsString());
            }
        });
    }

    @Override
    public void pushChatMessage(IChatMessage message, IChatChannel channel) {
        JsonObject json = new JsonObject();

        json.addProperty("message_type", MessageType.CHAT_MESSAGE.name());
        json.addProperty("chat_message", GSON.toJson(message));
        json.addProperty("chat_channel", GSON.toJson(channel));

        pushMessage(json);
    }

    @Override
    public void pushOnlinePlayers(IOnlinePlayers onlinePlayers) {
        JsonObject json = new JsonObject();

        json.addProperty("message_type", MessageType.ONLINE_PLAYERS.name());

        pushMessage(json);
    }

    @Override
    public void pushPlayerUpdate(IChatPlayer player) {
        JsonObject json = new JsonObject();

        json.addProperty("message_type", MessageType.PLAYER_UPDATE.name());

        pushMessage(json);
    }

    @Override
    public void handleChatMessage(IChatMessage message, IChatChannel channel) {
        channel.sendToChat(message);
    }

    @Override
    public void handleOnlinePlayers(IOnlinePlayers onlinePlayers) {
        // TODO:
    }

    @Override
    public void handlePlayerUpdate(IChatPlayer player) {
        // TODO:
    }

    @Override
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            jedisPool = null;
        }

        if (subscriber != null && subscriber.isConnected()) {
            subscriber.close();
            subscriber = null;
        }
    }

    private enum MessageType {
        CHAT_MESSAGE,
        ONLINE_PLAYERS,
        PLAYER_UPDATE
    }

    private static class RedisSubscriber extends JedisPubSub {
        private static final JsonParser PARSER = new JsonParser();

        private final RedisMessageBroker parent;

        public RedisSubscriber(RedisMessageBroker parent) {
            this.parent = parent;
        }

        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(CHANNEL)) {
                return;
            }

            JsonObject jsonMessage;

            try {
                jsonMessage = PARSER.parse(message).getAsJsonObject();
            } catch (JsonParseException e) {
                e.printStackTrace();
                return;
            }

            MessageType mType = MessageType.valueOf(jsonMessage.get("message_type").getAsString());

            switch (mType) {
                case CHAT_MESSAGE:
                    IChatMessage chatMessage = null;
                    IChatChannel chatChannel = null;

                    parent.handleChatMessage(chatMessage, chatChannel);
                    break;
                case ONLINE_PLAYERS:
                    IOnlinePlayers onlinePlayers = null;

                    parent.handleOnlinePlayers(onlinePlayers);
                    break;
                case PLAYER_UPDATE:
                    IChatPlayer player = null;

                    parent.handlePlayerUpdate(player);
                    break;
            }
        }
    }

}
