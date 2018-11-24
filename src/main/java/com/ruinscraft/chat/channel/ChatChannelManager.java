package com.ruinscraft.chat.channel;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.channel.types.pm.PrivateMessageChatChannel;
import com.ruinscraft.chat.message.ChatMessage;

public class ChatChannelManager {

	private Set<ChatChannel<?>> channels;
	
	public ChatChannelManager(ConfigurationSection channelSection) {
		channels = new HashSet<>();
		
		channels.add(new GlobalChatChannel());

		switch (channelSection.getString("local.type")) {
		case "default":
			channels.add(new DefaultLocalChatChannel());
			break;
		}
		
		channels.add(new PrivateMessageChatChannel(channelSection.getConfigurationSection("private-message")));

		channels.forEach(c -> c.registerCommands());
	}

	public <T extends ChatMessage> ChatChannel<T> getByName(String name) {
		for (ChatChannel<?> chatChannel : channels) {
			if (chatChannel.getName().equalsIgnoreCase(name)) {
				return (ChatChannel<T>) chatChannel;
			}
		}
		
		return (ChatChannel<T>) new GlobalChatChannel();
	}

}
