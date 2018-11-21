package com.ruinscraft.chat.messenger.redis;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageConsumer;

import redis.clients.jedis.JedisPubSub;

public class RedisMessageConsumer extends JedisPubSub implements MessageConsumer {

	private static final Gson GSON = new Gson();

	@Override
	public void onMessage(String channel, String messageRaw) {
		if (!channel.equals(RedisMessageManager.REDIS_CHAT_CHANNEL)) {
			return;
		}

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
		GenericChatMessage chatMessage = null;
		// TODO: handle other types of messages

		try {
			chatMessage = GSON.fromJson(message.getPayload(), GenericChatMessage.class);
		} catch (JsonParseException e) {
			e.printStackTrace();
		}

		if (chatMessage == null) {
			return;
		}

		System.out.println(chatMessage.getSender() + " > " + chatMessage.getPayload());
	}

}
