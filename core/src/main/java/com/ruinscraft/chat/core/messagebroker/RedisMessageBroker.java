package com.ruinscraft.chat.core.messagebroker;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.CompletableFuture;

public class RedisMessageBroker extends MessageBroker {

    private JedisPool jPool;
    private PubSub jPubSub;

    public RedisMessageBroker(String host, int port) {
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

        private PubSub(Jedis subscriber, String channel) {
            this.subscriber = subscriber;
            CompletableFuture.runAsync(() -> subscriber.subscribe(this, channel));
        }

        @Override
        public void onMessage(String channel, String message) {
            try {
                consume(Message.deserialize(message));
            } catch (Exception e) {
                // could not deserialize message
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
