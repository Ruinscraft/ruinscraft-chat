package com.ruinscraft.chat.channel;

import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.cache.LoadingCache;
import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.players.ChatPlayer;

public class ChatChannelManager {

	private ChatChannel globalChannel;
	private ChatChannel localChannel;
	
	private LoadingCache<String, ChatPlayer> cache;
	
	public ChatChannelManager(ConfigurationSection channelSection) {
		this.globalChannel = new GlobalChatChannel();
		
		switch (channelSection.getString("local-type")) {
		case "default":
			localChannel = new DefaultLocalChatChannel();
			break;
		}
	}
	
	public ChatChannel getGlobalChannel() {
		return globalChannel;
	}
	
	public ChatChannel getLocalChannel() {
		return localChannel;
	}
	
}
