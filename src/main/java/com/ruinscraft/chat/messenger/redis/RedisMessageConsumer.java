package com.ruinscraft.chat.messenger.redis;

import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageConsumer;

import redis.clients.jedis.JedisPubSub;

public class RedisMessageConsumer extends JedisPubSub implements MessageConsumer {

	@Override
	public void onMessage(String channel, String message) {
		
		consume(null);
		
	}
	
	@Override
	public void consume(Message message) {
		
	}
	
}
