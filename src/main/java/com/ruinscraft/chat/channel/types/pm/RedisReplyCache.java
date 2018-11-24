package com.ruinscraft.chat.channel.types.pm;

import org.bukkit.configuration.ConfigurationSection;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisReplyCache implements ReplyCache {

	private JedisPool pool;
	
	public RedisReplyCache(ConfigurationSection redisConfig) {
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
	public String getReply(String username) {
		return null;
	}

	@Override
	public void setReply(String username, String _username) {
		
	}
	
}
