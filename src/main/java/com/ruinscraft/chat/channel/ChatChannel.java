package com.ruinscraft.chat.channel;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageManager;

public interface ChatChannel<T extends ChatMessage> {

	String getName();

	String getFormat(T context);

	ChatColor getMessageColor();

	String getPermission();

	Command getCommand();

	boolean isLogged();

	default void send(T chatMessage) {
		MessageManager mm = ChatPlugin.getInstance().getMessageManager();
		Message message = new Message(chatMessage);

		mm.getDispatcher().dispatch(message);
	}

	default Set<UUID> getRecipients(UUID sender) {
		return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
	}

	default void registerCommands() {
		Command command = getCommand();
		
		Plugin plugin = ChatPlugin.getInstance();

		try {
			Method getCommandMap = plugin.getServer().getClass().getMethod("getCommandMap");
			getCommandMap.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
