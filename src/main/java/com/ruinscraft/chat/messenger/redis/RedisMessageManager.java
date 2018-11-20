package com.ruinscraft.chat.messenger.redis;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.messenger.MessageManager;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisMessageManager extends MessageManager {

	protected static final String REDIS_CHAT_CHANNEL = "rcchat";
	protected static JedisPool pool;

	public RedisMessageManager(ConfigurationSection messagingConfig) {
		if (messagingConfig.getBoolean("redis.use")) {
			String address = messagingConfig.getString("redis.address");
			int port = messagingConfig.getInt("redis.port");
			char[] password = messagingConfig.getString("redis.password").toCharArray();

			pool = new JedisPool(
					new JedisPoolConfig(),
					address,
					port == 0 ? Protocol.DEFAULT_PORT : port,
							Protocol.DEFAULT_TIMEOUT,
							password == null ? "" : new String(password));
			
			try (Jedis jedis = pool.getResource()) {
				jedis.ping();
				jedis.subscribe(new RedisMessageConsumer(), REDIS_CHAT_CHANNEL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close() throws Exception {
		pool.close();
	}

}
