package com.ruinscraft.chat.channel.types.pm;

import java.util.concurrent.Callable;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.ChatPlugin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisReplyStorage implements ReplyStorage {

	private static final String REPLY = "reply";
	private static final int EXPIRE_AFTER_SEC = 1200;
	
	private JedisPool pool;
	
	public RedisReplyStorage(ConfigurationSection redisConfig) {
		String address = redisConfig.getString("address");
		int port = redisConfig.getInt("port");
		String password = redisConfig.getString("password");

		pool = new JedisPool(
				new JedisPoolConfig(),
				address,
				port == 0 ? Protocol.DEFAULT_PORT : port,
						Protocol.DEFAULT_TIMEOUT,
						password);
	}
	
	@Override
	public Callable<String> getReply(String username) {
		return new Callable<String>() {
			@Override
			public String call() throws Exception {
				try (Jedis jedis = pool.getResource()) {
					return jedis.get(REPLY + "." + username);
				}
			}
		};
	}

	@Override
	public void setReply(String username, String _username) {
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try (Jedis jedis = pool.getResource()) {
				jedis.setex(REPLY + "." + username, EXPIRE_AFTER_SEC, _username);
				jedis.setex(REPLY + "." + _username, EXPIRE_AFTER_SEC, username);
			}
		});
	}
	
	@Override
	public void close() {
		pool.close();
	}
	
}
