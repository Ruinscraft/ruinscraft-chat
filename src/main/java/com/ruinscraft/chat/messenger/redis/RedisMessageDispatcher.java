package com.ruinscraft.chat.messenger.redis;

import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import redis.clients.jedis.Jedis;

public class RedisMessageDispatcher implements MessageDispatcher {

    private RedisMessageManager manager;

    public RedisMessageDispatcher(RedisMessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void dispatch(Message message) {
        try (Jedis jedis = manager.getJedisPool().getResource()) {
            jedis.publish(RedisMessageManager.REDIS_CHAT_CHANNEL, message.serialize());
        }
    }

}
