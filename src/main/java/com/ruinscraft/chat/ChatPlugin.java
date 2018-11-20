package com.ruinscraft.chat;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.logging.ChatLogger;
import com.ruinscraft.chat.players.ChatPlayerManager;

public class ChatPlugin extends JavaPlugin {
	
	private static ChatPlugin instance;
	
	public static ChatPlugin getInstance() {
		return instance;
	}
	
	/*
	 * When a message is received by the MessageConsumer,
	 * if it is a ChatMessage, it will have an associated
	 * server where the ChatMessage needs to be received.
	 * The server must be aware of what "server" it actually
	 * is.
	 */
	private String serverName;
	
	private ChatPlayerManager chatPlayerManager;
	private ChatChannelManager chatChannelManager;
	
	// TODO: implement logging
	private ChatLogger logging;
	
	@Override
	public void onEnable() {
		instance = this;
		
		if (getServer().getPluginManager().getPlugin("ruinscraft-player-status") == null) {
			getLogger().warning("ruinscraft-player-status required");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
	}
	
	@Override
	public void onDisable() {
		try {
			chatPlayerManager.close();
			logging.close();
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
