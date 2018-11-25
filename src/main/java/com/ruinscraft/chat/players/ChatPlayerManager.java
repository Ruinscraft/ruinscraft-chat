package com.ruinscraft.chat.players;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.players.storage.ChatPlayerStorage;
import com.ruinscraft.chat.players.storage.MySQLChatPlayerStorage;

public class ChatPlayerManager implements AutoCloseable {

	private BlockingQueue<UUID> toLoad;
	private BlockingQueue<UUID> toSave;
	private LoadingCache<UUID, ChatPlayer> cache;
	private ChatPlayerStorage storage;

	public ChatPlayerManager(ConfigurationSection playerStorageSection) {
		/* Setup cache*/
		toLoad = new ArrayBlockingQueue<>(256);
		toSave = new ArrayBlockingQueue<>(256);
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
		
		/* Setup cache loader */
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			while (toLoad != null) {
				try {
					cache.get(toLoad.take());
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		/* Setup saving task */
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			while (toSave != null) {
				try {
					ChatPlayer saving = cache.getIfPresent(toSave.take());
					
					if (saving != null) {
						storage.saveChatPlayer(saving).call();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Schedule loading in a ChatPlayer from a UUID asynchronously. Non-blocking
	 * 
	 * @param 	uuid	UUID of the player
	 */
	public void loadChatPlayer(UUID uuid) {
		try {
			toLoad.put(uuid);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Unload a UUID immediately if exists. Non-blocking
	 * 
	 * @param 	uuid	UUID of the player
	 */
	public void unloadChatPlayer(UUID uuid) {
		cache.invalidate(uuid);
	}

	/**
	 * Get a ChatPlayer from a UUID. Non-blocking
	 * 
	 * @param 	uuid	UUID of the player
	 * @return	the ChatPlayer associated with the UUID, null if not loaded
	 */
	public ChatPlayer getChatPlayer(UUID uuid) {
		return cache.getIfPresent(uuid);
	}
	
	public void save(ChatPlayer chatPlayer) {
		try {
			toSave.put(chatPlayer.getMojangUUID());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		cache.invalidateAll();
		storage.close();
		toLoad.clear();
		toSave.clear();
		
		cache = null;
		storage = null;
		toLoad = null;
		toSave = null;
	}

	private final class ChatPlayerCacheLoader extends CacheLoader<UUID, ChatPlayer> {
		@Override
		public ChatPlayer load(UUID uuid) throws Exception {
			ChatPlayer chatPlayer = new ChatPlayer(uuid);

			storage.loadChatPlayer(chatPlayer).call();
			
			return chatPlayer;
		}
	}

}
