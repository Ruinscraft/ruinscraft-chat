package com.ruinscraft.chat.channel;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
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
import com.ruinscraft.chat.filters.ChatFilterManager;
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
	
	boolean isLoggedGlobally();

	default Callable<Void> filter(ChatChannelManager chatChannelManager, ChatFilterManager chatFilterManager, CommandSender sender, ChatMessage chatMessage) throws NotSendableException {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				for (ChatFilter filter : chatFilterManager.getChatFilters()) {
					chatMessage.setPayload(filter.filter(chatMessage.getPayload()));
				}

				return null;
			}
		};
	}

	default void dispatch(MessageDispatcher dispatcher, Player sender, boolean filter, T chatMessage) {
		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			if (filter) {
				try {
					filter(ChatPlugin.getInstance().getChatChannelManager(), ChatPlugin.getInstance().getChatFilterManager(), sender, chatMessage).call();
				} catch (NotSendableException e) {
					if (sender != null) {
						sender.sendMessage(ChatColor.RED + e.getMessage());
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			MessageManager mm = ChatPlugin.getInstance().getMessageManager();
			Message message = new Message(chatMessage);

			mm.getDispatcher().dispatch(message);
		});
	}

	default void sendToChat(T chatMessage) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			// if no permission defined or they have it
			if (getPermission() == null || onlinePlayer.hasPermission(getPermission())) {
				String format = getFormat(onlinePlayer.getName(), chatMessage);

				format = format
						.replace("%server%", chatMessage.getServerSentFrom())
						.replace("%prefix%", chatMessage.getSenderPrefix())
						.replace("%sender%", chatMessage.getSender());

				if (chatMessage.colorizePayload()) {
					format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', chatMessage.getPayload()));
				} else {
					format = format.replace("%message%", chatMessage.getPayload());
				}

				onlinePlayer.sendMessage(format);
			}
		}

		log(ChatPlugin.getInstance().getChatChannelManager(), chatMessage);
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
