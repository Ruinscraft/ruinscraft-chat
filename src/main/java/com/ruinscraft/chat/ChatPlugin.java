package com.ruinscraft.chat;

import org.bukkit.plugin.java.JavaPlugin;

import com.ruinscraft.chat.logging.ChatLogger;

public class ChatPlugin extends JavaPlugin {

	private static ChatPlugin instance;
	
	public static ChatPlugin getInstance() {
		return instance;
	}
	
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
			logging.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		instance = null;
	}
	
	public ChatLogger getLogging() {
		return logging;
	}
	
}
