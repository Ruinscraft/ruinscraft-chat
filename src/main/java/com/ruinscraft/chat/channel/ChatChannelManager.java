package com.ruinscraft.chat.channel;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.channel.types.MBAChatChannel;
import com.ruinscraft.chat.channel.types.MBChatChannel;
import com.ruinscraft.chat.channel.types.MBHChatChannel;
import com.ruinscraft.chat.channel.types.MBSChatChannel;
import com.ruinscraft.chat.channel.types.pm.PrivateMessageChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;

public class ChatChannelManager {

	private Set<ChatChannel<?>> channels;

	public ChatChannelManager(ConfigurationSection channelSection) {
		/* Setup channels */
		channels = new HashSet<>();
		channels.add(new GlobalChatChannel());
		switch (channelSection.getString("local.type")) {
		case "default":
			channels.add(new DefaultLocalChatChannel());
			break;
		}
		channels.add(new PrivateMessageChatChannel(channelSection.getConfigurationSection("private-message")));
		channels.add(new MBChatChannel());
		channels.add(new MBHChatChannel());
		channels.add(new MBSChatChannel());
		channels.add(new MBAChatChannel());
		channels.forEach(c -> c.registerCommands());
	}

	public <T extends ChatMessage> ChatChannel<T> getByName(String name) {
		for (ChatChannel<?> chatChannel : channels) {
			if (chatChannel.getName().equalsIgnoreCase(name)) {
				return (ChatChannel<T>) chatChannel;
			}
			
			if (chatChannel.getPrettyName().equalsIgnoreCase(name)) {
				return (ChatChannel<T>) chatChannel;
			}
		}

		return (ChatChannel<T>) new GlobalChatChannel();
	}

	public ChatChannel<GenericChatMessage> getDefaultChatChannel() {
		return getByName("global");
	}
	
	public Set<ChatChannel<?>> getChatChannels() {
		return channels;
	}
	
	public Set<ChatChannel<?>> getMuteableChannels() {
		Set<ChatChannel<?>> muteable = new HashSet<>();
		for (ChatChannel<?> channel : channels) {
			if (channel.muteable()) {
				muteable.add(channel);
			}
		}
		return muteable;
	}
	
	public void unregisterAll() {
		channels.forEach(c -> c.unregisterCommands());
		channels.clear();
	}

}
