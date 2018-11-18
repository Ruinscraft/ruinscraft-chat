package com.ruinscraft.chat.messenger.redis;

import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisMessageDispatcher implements MessageDispatcher {

	private JedisPool pool;

	public RedisMessageDispatcher() {
		// setup redis pool
	}

	@Override
	public void dispatch(Message message) {
		try (Jedis jedis = pool.getResource()) {
			
			
			
		}
	}

	@Override
	public void close() {
		pool.close();
	}

}
