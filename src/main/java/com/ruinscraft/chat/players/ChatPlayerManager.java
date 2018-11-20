package com.ruinscraft.chat.players;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.players.storage.ChatPlayerStorage;
import com.ruinscraft.chat.players.storage.MySQLChatPlayerStorage;

public class ChatPlayerManager implements AutoCloseable {

	private LoadingCache<UUID, ChatPlayer> cache;
	private ChatPlayerStorage storage;

	public ChatPlayerManager(ConfigurationSection playerStorageSection) {
		/* Setup cache*/
		cache = CacheBuilder.newBuilder()
				.build(new ChatPlayerCacheLoader());

		/* Setup storage */
		if (playerStorageSection.getBoolean("mysql.use")) {
			String address = playerStorageSection.getString("mysql.address");
			int port = playerStorageSection.getInt("mysql.port");
			String database = playerStorageSection.getString("mysql.database");
			String username = playerStorageSection.getString("mysql.username");
			char[] password = playerStorageSection.getString("mysql.password").toCharArray();

			storage = new MySQLChatPlayerStorage(address, port, database, username, password);
			
			ChatPlugin.info("Using MySQL for player storage");
		}
	}

	public void loadChatPlayer(UUID uuid) {
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try {
				cache.get(uuid);
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		});
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
