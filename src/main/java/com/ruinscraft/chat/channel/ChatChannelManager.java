package com.ruinscraft.chat.channel;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class ChatChannelManager {

	private ChatChannel<GenericChatMessage> globalChannel;
	private ChatChannel<GenericChatMessage> localChannel;

	public ChatChannelManager(ConfigurationSection channelSection) {
		this.globalChannel = new GlobalChatChannel();

		switch (channelSection.getString("local-type")) {
		case "default":
			localChannel = new DefaultLocalChatChannel();
			break;
		}
	}

	public ChatChannel<GenericChatMessage> getGlobalChannel() {
		return globalChannel;
	}

	public ChatChannel<GenericChatMessage> getLocalChannel() {
		return localChannel;
	}

	// TODO: make this better
	public ChatChannel<?> getByName(String name) {
		if (name.equalsIgnoreCase("global")) {
			return globalChannel;
		}

		else if (name.equalsIgnoreCase("local")) {
			return localChannel;
		}

		return globalChannel;
	}

}
