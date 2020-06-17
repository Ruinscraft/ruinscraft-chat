package com.ruinscraft.chat.core.messagebroker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ruinscraft.chat.api.messagebroker.MessageType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public class RedisJsonMessageBroker extends JsonMessageBroker {

    private JedisPool jPool;
    private PubSub jPubSub;

    public RedisJsonMessageBroker(String host, int port) {
        JedisPoolConfig jPoolConfig = new JedisPoolConfig();
        jPoolConfig.setMaxIdle(16);
        jPoolConfig.setMaxTotal(64);

        jPool = new JedisPool(host, port);
        jPubSub = new PubSub(jPool.getResource(), "ruinscraft-chat");
    }

    @Override
    public void close() {
        if (jPool != null) {
            jPool.close();
        }

        if (jPubSub != null) {
            jPubSub.close();
        }
    }

    private final class PubSub extends JedisPubSub {
        private Jedis subscriber;

        private final JsonParser parser = new JsonParser();

        private PubSub(Jedis subscriber, String channel) {
            this.subscriber = subscriber;
            CompletableFuture.runAsync(() -> subscriber.subscribe(this, channel));
        }

        @Override
        public void onMessage(String channel, String message) {
            try {
                JsonObject json = parser.parse(message).getAsJsonObject();
                MessageType type = MessageType.valueOf(json.get("type").getAsString());
                JsonObject payload = json.get("payload").getAsJsonObject();
                JsonMessage jsonMessage = new JsonMessage(type, payload);

                consume(jsonMessage);
            } catch (Exception e) {
                // message was not json
                e.printStackTrace();
            }
        }

        public void close() {
            if (subscriber != null && subscriber.isConnected()) {
                subscriber.close();
            }
        }
    }

}
