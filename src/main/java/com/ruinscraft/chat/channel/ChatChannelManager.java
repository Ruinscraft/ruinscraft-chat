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
import com.ruinscraft.chat.filters.CapsFilter;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.LengthFilter;
import com.ruinscraft.chat.logging.ChatLogger;
import com.ruinscraft.chat.logging.ConsoleChatLogger;
import com.ruinscraft.chat.message.ChatMessage;

public class ChatChannelManager {

	private Set<ChatChannel<?>> channels;
	private Set<ChatLogger> loggers;
	private Set<ChatFilter> filters;

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

		/* Setup loggers */
		loggers = new HashSet<>();
		loggers.add(new ConsoleChatLogger());

		/* Setup filters */
		filters = new HashSet<>();
		filters.add(new CapsFilter());
		filters.add(new LengthFilter());
	}

	public <T extends ChatMessage> ChatChannel<T> getByName(String name) {
		for (ChatChannel<?> chatChannel : channels) {
			if (chatChannel.getName().equalsIgnoreCase(name)) {
				return (ChatChannel<T>) chatChannel;
			}
		}

		return (ChatChannel<T>) new GlobalChatChannel();
	}

	public Set<ChatLogger> getChatLoggers() {
		return loggers;
	}

	public Set<ChatFilter> getChatFilters() {
		return filters;
	}

}
