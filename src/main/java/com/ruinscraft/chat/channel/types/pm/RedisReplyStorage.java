package com.ruinscraft.chat.channel.types.pm;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.bukkit.configuration.ConfigurationSection;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol;

public class RedisReplyStorage implements ReplyStorage {

	private static final String REPLY = "reply";
	private static final int EXPIRE_AFTER_SEC = 1200;

	private JedisPool pool;

	public RedisReplyStorage(ConfigurationSection redisConfig) {
		String address = redisConfig.getString("address");
		int port = redisConfig.getInt("port");
		JedisPoolConfig config = new JedisPoolConfig();
		pool = new JedisPool(config, address, port == 0 ? Protocol.DEFAULT_PORT : port);

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
	public Callable<Void> setReply(String username, String _username) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (Jedis jedis = pool.getResource()) {
					try (Pipeline pipeline = jedis.pipelined()) {
						pipeline.setex(REPLY + "." + username, EXPIRE_AFTER_SEC, _username);
						pipeline.setex(REPLY + "." + _username, EXPIRE_AFTER_SEC, username);
						pipeline.sync();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
	}

	@Override
	public void close() {
		if (!pool.isClosed()) {
			pool.close();
		}
	}

}
