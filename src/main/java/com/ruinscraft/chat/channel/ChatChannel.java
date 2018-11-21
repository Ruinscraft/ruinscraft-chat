package com.ruinscraft.chat.channel;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageManager;

public interface ChatChannel<T> {

	String getName();

	String getFormat(T context);

	ChatColor getMessageColor();

	String getPermission();

	String[] getCommands();

	boolean isOneToOne();

	boolean isLogged();

	default void send(T chatMessage) {
		MessageManager mm = ChatPlugin.getInstance().getMessageManager();
		Message message = new Message(chatMessage);

		mm.getDispatcher().dispatch(message);
	}

	default Set<UUID> getRecipients(UUID sender) {
		return Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
	}

}
