package com.ruinscraft.chat.players;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ruinscraft.chat.players.storage.ChatPlayerStorage;
import com.ruinscraft.chat.players.storage.MySQLChatPlayerStorage;

public class ChatPlayerManager implements AutoCloseable {

	private LoadingCache<UUID, ChatPlayer> cache;
	private ChatPlayerStorage storage;

	public ChatPlayerManager(ConfigurationSection storageConfig) {
		/* Setup cache*/
		cache = CacheBuilder.newBuilder()
				.build(new ChatPlayerCacheLoader());

		/* Setup storage */
		if (storageConfig.getBoolean("mysql.use")) {
			String address = storageConfig.getString("mysql.address");
			int port = storageConfig.getInt("mysql.port");
			String database = storageConfig.getString("mysql.database");
			String username = storageConfig.getString("mysql.username");
			char[] password = storageConfig.getString("mysql.password").toCharArray();

			storage = new MySQLChatPlayerStorage(address, port, database, username, password);
		}
	}

	public void loadChatPlayer(UUID uuid) {
		// TODO: does this work?
		cache.refresh(uuid);
	}

	public void unloadChatPlayer(UUID uuid) {
		cache.invalidate(uuid);
	}

	public ChatPlayer getChatPlayer(UUID uuid) {
		return cache.getIfPresent(uuid);
	}

	@Override
	public void close() throws Exception {
		cache.invalidateAll();
		storage.close();
	}

	private final class ChatPlayerCacheLoader extends CacheLoader<UUID, ChatPlayer> {
		@Override
		public ChatPlayer load(UUID uuid) throws Exception {
			ChatPlayer chatPlayer = new ChatPlayer();

			/* Blocking */
			storage.loadChatPlayer(chatPlayer);

			return chatPlayer;
		}
	}

}
