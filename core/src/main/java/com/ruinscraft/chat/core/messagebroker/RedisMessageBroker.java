package com.ruinscraft.chat.core.messagebroker;

import redis.clients.jedis.JedisPool;

public class RedisMessageBroker extends MessageBroker {

    private JedisPool jedisPool;

    public RedisMessageBroker(String host, int port) {
        
    }

    @Override
    public void close() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

}
