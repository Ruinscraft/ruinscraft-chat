package com.ruinscraft.chat.messenger.redis;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageConsumer;

import redis.clients.jedis.JedisPubSub;

public class RedisMessageConsumer extends JedisPubSub implements MessageConsumer {

	private static final Gson GSON = new Gson();

	@Override
	public void onMessage(String channel, String messageRaw) {
		Message message = null;

		try {
			message = GSON.fromJson(messageRaw, Message.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

		if (message != null) {
			consume(message);
		}
	}

	@Override
	public void consume(Message message) {
		if (message.getPayload() instanceof ChatMessage) {
			System.out.println("received chat message: " + message.getPayload());
		}
	}

}
