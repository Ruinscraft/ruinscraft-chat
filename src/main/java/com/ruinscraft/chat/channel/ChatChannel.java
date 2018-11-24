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

	default void send(CommandSender caller, T chatMessage) {
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
			Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");

			bukkitCommandMap.setAccessible(true);
			
			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());

			commandMap.register(ChatPlugin.RUINSCRAFT_CHAT, command);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
