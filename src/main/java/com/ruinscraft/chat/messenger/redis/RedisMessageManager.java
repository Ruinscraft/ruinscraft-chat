package com.ruinscraft.chat.messenger.redis;

import com.ruinscraft.chat.messenger.MessageConsumer;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.chat.messenger.MessageManager;
import org.bukkit.configuration.ConfigurationSection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisMessageManager implements MessageManager {

    protected static final String REDIS_CHAT_CHANNEL = "rcchat";
    private JedisPool pool;
    private Jedis subscriber;

    private RedisMessageConsumer consumer;
    private RedisMessageDispatcher dispatcher;

    public RedisMessageManager(ConfigurationSection redisConfig) {
        consumer = new RedisMessageConsumer();
        dispatcher = new RedisMessageDispatcher(this);

        String address = redisConfig.getString("address");
        int port = redisConfig.getInt("port");

        JedisPoolConfig config = new JedisPoolConfig();

        pool = new JedisPool(config, address, port == 0 ? Protocol.DEFAULT_PORT : port);
        subscriber = pool.getResource();

        subscriber.connect();
        subscriber.subscribe(consumer, REDIS_CHAT_CHANNEL);
    }

    @Override
    public MessageConsumer getConsumer() {
        return consumer;
    }

    @Override
    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    protected JedisPool getJedisPool() {
        return pool;
    }

    @Override
    public void close() {
        if (consumer.isSubscribed()) {
            consumer.unsubscribe();
        }

        if (!pool.isClosed()) {
            pool.close();
        }

        if (subscriber.isConnected()) {
            subscriber.close();
        }
    }

}
