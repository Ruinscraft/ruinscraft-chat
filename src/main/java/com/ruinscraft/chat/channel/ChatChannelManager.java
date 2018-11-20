package com.ruinscraft.chat.channel;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;

public class ChatChannelManager {

	private ChatChannel globalChannel;
	private ChatChannel localChannel;
	
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
