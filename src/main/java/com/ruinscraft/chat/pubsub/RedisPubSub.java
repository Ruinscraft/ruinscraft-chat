package com.ruinscraft.chat.pubsub;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.ChatPlayer;
import com.ruinscraft.chat.ChatPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public class RedisPubSub extends PubSub {

    private static final String CHANNEL = "ruinscraft-chat";

    private final String host;
    private final int port;

    private JedisPool jedisPool;
    private JedisSubscriber jedisSubscriber;

    private ChatPlugin chatPlugin;

    public RedisPubSub(String host, int port, ChatPlugin chatPlugin) {
        this.host = host;
        this.port = port;
        this.chatPlugin = chatPlugin;

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

        jedisPoolConfig.setMaxIdle(32);
        jedisPoolConfig.setMaxTotal(64);

        jedisPool = new JedisPool(jedisPoolConfig, host, port);

        jedisSubscriber = new JedisSubscriber(jedisPool.getResource());
    }

    private CompletableFuture<Void> publishMessage(String json) {
        return CompletableFuture.runAsync(() -> {
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(CHANNEL, json);
            }
        });
    }

    @Override
    protected CompletableFuture<Void> publishChatPlayerLogin(String json) {
        return publishMessage(json);
    }

    @Override
    protected CompletableFuture<Void> publishChatPlayerLogout(String json) {
        return publishMessage(json);
    }

    @Override
    protected CompletableFuture<Void> publishChatMessage(String json) {
        return publishMessage(json);
    }

    @Override
    protected CompletableFuture<Void> publishFriendRequest(String json) {
        return publishMessage(json);
    }

    @Override
    protected CompletableFuture<Void> publishFriendRequestResponse(String json) {
        return publishMessage(json);
    }

    @Override
    public void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
            jedisPool = null;
        }

        if (jedisSubscriber != null) {
            jedisSubscriber.close();
            jedisSubscriber = null;
        }
    }

    private class JedisSubscriber extends JedisPubSub {
        private final JsonParser JSON_PARSER = new JsonParser();

        private Jedis jedis;

        public JedisSubscriber(Jedis jedis) {
            this.jedis = jedis;

            CompletableFuture.runAsync(() -> {
                jedis.subscribe(this, CHANNEL);
            });
        }

        @Override
        public void onMessage(String channel, String message) {
            JsonElement jsonElement;

            try {
                jsonElement = JSON_PARSER.parse(message);
            } catch (JsonParseException e) {
                e.printStackTrace();
                return;w
            }

            if (!jsonElement.isJsonObject()) {
                return;
            }

            JsonObject jsonObject = jsonElement.getAsJsonObject();
            MessageType messageType = MessageType.valueOf(jsonObject.get("message_type").getAsString());

            switch (messageType) {
                case MESSAGE_CHAT_PLAYER_LOGIN:
                    handleChatPlayerLogin(ChatPlayer.deserialize(chatPlugin, jsonObject));
                    break;
                case MESSAGE_CHAT_PLAYER_LOGOUT:
                    handleChatPlayerLogout(ChatPlayer.deserialize(chatPlugin, jsonObject));
                    break;
                case MESSAGE_CHAT_MESSAGE:
                    handleChatMessage(ChatMessage.deserialize(chatPlugin, jsonObject));
                    break;
            }
        }

        public void close() {
            unsubscribe();
            jedis.close();
        }
    }

}
