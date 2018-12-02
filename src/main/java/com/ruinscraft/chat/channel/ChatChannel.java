package com.ruinscraft.chat.channel;

import java.lang.reflect.Field;
import java.util.HashMap;
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
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.ChatFilterManager;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.chat.messenger.MessageManager;
import com.ruinscraft.chat.players.ChatPlayer;

public interface ChatChannel<T extends ChatMessage> {

	String getName();

	String getPrettyName();

	String getFormat(String viewer, T context);

	ChatColor getMessageColor();

	String getPermission();

	Command getCommand();

	boolean isLogged();

	boolean isLoggedGlobally();

	boolean muteable();

	boolean spyable();

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

	default Callable<Void> dispatch(MessageDispatcher dispatcher, Player sender, boolean filter, T chatMessage) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(sender.getUniqueId());

				if (chatPlayer.isMuted(ChatChannel.this)) {
					sender.sendMessage(Constants.COLOR_ERROR + "You have this channel muted. Unmute it with /chat");
					return null;
				}

				if (filter) {
					try {
						filter(ChatPlugin.getInstance().getChatChannelManager(), ChatPlugin.getInstance().getChatFilterManager(), sender, chatMessage).call();
					} catch (NotSendableException e) {
						if (sender != null) {
							sender.sendMessage(Constants.COLOR_ERROR + e.getMessage());
							return null;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				MessageManager mm = ChatPlugin.getInstance().getMessageManager();
				Message message = new Message(chatMessage);

				mm.getDispatcher().dispatch(message);
				return null;
			}
		};
	}

	default void sendToChat(T chatMessage) {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(onlinePlayer.getUniqueId());

			if (chatPlayer.isMuted(this)) {
				continue;
			}

			if (chatPlayer.isIgnoring(chatMessage.getSender())) {
				continue;
			}

			if (chatPlayer.isIgnoring(chatMessage.getSenderUUID())) {
				continue;
			}

			// if no permission defined or they have it
			if (getPermission() == null || onlinePlayer.hasPermission(getPermission())) {
				String format = getFormat(onlinePlayer.getName(), chatMessage);

				format = format
						.replace("%server%", chatMessage.getServerSentFrom())
						.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', chatMessage.getSenderPrefix()))
						.replace("%sender%", chatMessage.getSender());

				if (chatMessage.colorizePayload()) {
					format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', chatMessage.getPayload()));
				} else {
					format = format.replace("%message%", chatMessage.getPayload());
				}

				onlinePlayer.sendMessage(format);
			}
		}

		logAsync(chatMessage);
	}

	default void logAsync(final T chatMessage) {
		if (isLogged()) {
			/* Prevent multiple servers from logging the same message, only log from the server the sender is on */
			Player sender = Bukkit.getPlayer(chatMessage.getSender());

			if (sender == null || !sender.isOnline()) {
				return;
			}

			ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
				ChatPlugin.getInstance().getChatLoggingManager().getChatLoggers().forEach(l -> {
					try {
						l.log(chatMessage).call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			});
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

			commandMap.register(Constants.STRING_RUINSCRAFT_CHAT_PLUGIN_NAME, command);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	default void unregisterCommands() {
		Command command = getCommand();

		if (command == null) {
			return;
		}

		Plugin plugin = ChatPlugin.getInstance();

		try {
			Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
			bukkitCommandMap.setAccessible(true);

			CommandMap commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());

			command.unregister(commandMap);

			Field commandMapKnownCommands = commandMap.getClass().getDeclaredField("knownCommands");
			commandMapKnownCommands.setAccessible(true);

			HashMap<String, Command> knownCommands = (HashMap<String, Command>) commandMapKnownCommands.get(commandMap);

			knownCommands.remove(command.getName());

			for (String alias : command.getAliases()) {
				knownCommands.remove(alias);
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
