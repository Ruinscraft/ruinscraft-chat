package com.ruinscraft.chat.channel;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.channel.types.pm.PrivateMessageChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public class ChatChannelManager {

	private ChatChannel<GenericChatMessage> globalChannel;
	private ChatChannel<GenericChatMessage> localChannel;
	private ChatChannel<PrivateChatMessage> privateMessageChannel;

	public ChatChannelManager(ConfigurationSection channelSection) {
		this.globalChannel = new GlobalChatChannel();

		switch (channelSection.getString("local.type")) {
		case "default":
			localChannel = new DefaultLocalChatChannel();
			break;
		}
		
		this.privateMessageChannel = new PrivateMessageChatChannel(channelSection.getConfigurationSection("private-message"));
		privateMessageChannel.registerCommands();
	}

	public ChatChannel<GenericChatMessage> getGlobalChannel() {
		return globalChannel;
	}

	public ChatChannel<GenericChatMessage> getLocalChannel() {
		return localChannel;
	}
	
	public ChatChannel<PrivateChatMessage> getPrivateMessageChannel() {
		return privateMessageChannel;
	}

	// TODO: make this better
	public ChatChannel<?> getByName(String name) {
		if (name.equalsIgnoreCase("global")) {
			return globalChannel;
		}

		else if (name.equalsIgnoreCase("local")) {
			return localChannel;
		}
		
		else if (name.equalsIgnoreCase("pm")) {
			return privateMessageChannel;
		}

		return globalChannel;
	}

}
