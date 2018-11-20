package com.ruinscraft.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.listeners.ChatListener;
import com.ruinscraft.chat.listeners.QuitJoinListener;
import com.ruinscraft.chat.logging.ChatLogger;
import com.ruinscraft.chat.messenger.MessageManager;
import com.ruinscraft.chat.messenger.redis.RedisMessageManager;
import com.ruinscraft.chat.players.ChatPlayerManager;

public class ChatPlugin extends JavaPlugin {
	
	private static ChatPlugin instance;
	
	public static ChatPlugin getInstance() {
		return instance;
	}
	
	public static void info(String message) {
		instance.getLogger().info(message);
	}
	
	public static void warning(String message) {
		instance.getLogger().warning(message);
	}
	
	/*
	 * When a message is received by the MessageConsumer,
	 * if it is a ChatMessage, it will have an associated
	 * server where the ChatMessage needs to be received.
	 * The server must be aware of what "server" it actually
	 * is.
	 */
	private String serverName;
	private MessageManager messageManager;
	private ChatPlayerManager chatPlayerManager;
	private ChatChannelManager chatChannelManager;
	private ChatLogger logging; // TODO: implement logging
	
	@Override
	public void onEnable() {
		instance = this;
		
		PluginManager pm = getServer().getPluginManager();

		saveDefaultConfig();
		
		/* Check for ruinscraft-player-status */
		if (pm.getPlugin("ruinscraft-player-status") == null) {
			warning("ruinscraft-player-status required");
			pm.disablePlugin(this);
			return;
		}
		
		/* Setup MessageManager*/
		info("Setting up MessageManager");
		ConfigurationSection messagingSection = getConfig().getConfigurationSection("messaging");
		if (messagingSection.getBoolean("redis.use")) {
			messageManager = new RedisMessageManager(messagingSection.getConfigurationSection("redis"));
		}
		
		info("Setting up ChatPlayerManager");
		/* Setup ChatPlayerManager */
		chatPlayerManager = new ChatPlayerManager(getConfig().getConfigurationSection("player-storage"));
		
		info("Setting up ChannelManager");
		/* Setup ChatChannelManager */
		chatChannelManager = new ChatChannelManager(getConfig().getConfigurationSection("channels"));
		
		
		/* Register Bukkit Listeners */
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new QuitJoinListener(), this);
	}
	
	@Override
	public void onDisable() {
		try {
			messageManager.close();
			chatPlayerManager.close();
			//logging.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		instance = null;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	
	public String getServerName() {
		return serverName;
	}
	
	public MessageManager getMessageManager() {
		return messageManager;
	}
	
	public ChatPlayerManager getChatPlayerManager() {
		return chatPlayerManager;
	}

	public ChatChannelManager getChatChannelManager() {
		return chatChannelManager;
	}
	
	public ChatLogger getLogging() {
		return logging;
	}
	
}
