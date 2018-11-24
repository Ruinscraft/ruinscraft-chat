package com.ruinscraft.chat.channel;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.chat.messenger.MessageManager;

public interface ChatChannel<T extends ChatMessage> {

	String getName();

	String getFormat(String viewer, T context);

	ChatColor getMessageColor();

	String getPermission();

	Command getCommand();

	boolean isLogged();

	default void dispatch(MessageDispatcher dispatcher, CommandSender caller, T chatMessage) {
		MessageManager mm = ChatPlugin.getInstance().getMessageManager();
		Message message = new Message(chatMessage);

		mm.getDispatcher().dispatch(message);
	}

	default void sendToChat(ChatChannelManager chatChannelManager, T chatMessage) {
		String message = chatMessage.getPayload();
		
		for (ChatFilter filter : chatChannelManager.getChatFilters()) {
			try {
				message = filter.filter(message);
			} catch (NotSendableException e) {
				Player sender = Bukkit.getPlayerExact(chatMessage.getSender());
				
				if (sender != null && sender.isOnline()) {
					sender.sendMessage(e.getMessage());
				}
				
				return;
			}
		}
		
		Bukkit.getOnlinePlayers().forEach(p -> {
			if (getPermission() == null || p.hasPermission(getPermission())) {
				p.sendMessage(chatMessage.getSender() + " > " + chatMessage.getPayload());
			}
		});

		log(chatChannelManager, chatMessage);
	}

	default void log(ChatChannelManager chatChannelManager, T chatMessage) {
		if (isLogged()) {
			chatChannelManager.getChatLoggers().forEach(l -> l.log(chatMessage));
		}
	}

	default Set<UUID> getRecipients(UUID sender) {
		return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
	}

	default void registerCommands() {
		Command command = getCommand();

		if (command == null) {
			return;
		}

		Plugin plugin = ChatPlugin.getInstance();

		try {
			Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);

			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());

			commandMap.register(ChatPlugin.RUINSCRAFT_CHAT, command);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
