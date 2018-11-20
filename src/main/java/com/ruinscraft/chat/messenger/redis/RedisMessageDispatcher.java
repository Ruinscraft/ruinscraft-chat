package com.ruinscraft.chat.messenger.redis;

import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;

import redis.clients.jedis.Jedis;

public class RedisMessageDispatcher implements MessageDispatcher {

	@Override
	public void dispatch(Message message) {
		try (Jedis jedis = RedisMessageManager.pool.getResource()) {
			jedis.publish(RedisMessageManager.REDIS_CHAT_CHANNEL, message.serialize());
		}
	}

}
