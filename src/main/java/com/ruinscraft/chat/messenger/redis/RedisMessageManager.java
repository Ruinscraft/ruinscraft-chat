package com.ruinscraft.chat.messenger.redis;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.messenger.MessageConsumer;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.chat.messenger.MessageManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisMessageManager implements MessageManager {

	protected static final String REDIS_CHAT_CHANNEL = "rcchat";
	protected static JedisPool pool;

	private RedisMessageConsumer consumer;
	private RedisMessageDispatcher dispatcher;

	public RedisMessageManager(ConfigurationSection redisConfig) {
		consumer = new RedisMessageConsumer();
		dispatcher = new RedisMessageDispatcher();
		
		String address = redisConfig.getString("address");
		int port = redisConfig.getInt("port");
		String password = redisConfig.getString("password");

		pool = new JedisPool(
				new JedisPoolConfig(),
				address,
				port == 0 ? Protocol.DEFAULT_PORT : port,
						Protocol.DEFAULT_TIMEOUT,
						password);
		
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try (Jedis jedis = pool.getResource()) {
				jedis.subscribe(consumer, REDIS_CHAT_CHANNEL);
			}
		});
	}

	@Override
	public MessageConsumer getConsumer() {
		return consumer;
	}

	@Override
	public MessageDispatcher getDispatcher() {
		return dispatcher;
	}

	@Override
	public void close() {
		consumer.unsubscribe();
		pool.close();
	}

}
