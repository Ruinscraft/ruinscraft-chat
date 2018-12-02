package com.ruinscraft.chat.channel.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class DefaultLocalChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "local";
	}
	
	@Override
	public String getPrettyName() {
		return "Local Chat (the server you are on)";
	}

	@Override
	public String getFormat(String viewer, GenericChatMessage context) {
		String noColor = "";
		if (context.getSenderNickname() != null) {
			noColor = "&b[L] &7[%prefix%&7] %sender% &8&l>&r &6(%nickname%)&r" + getMessageColor() + " %message%";
		} else {
			noColor = "&b[L] &7[%prefix%&7] %sender% &8&l>&r" + getMessageColor() + " %message%";
		}
		return ChatColor.translateAlternateColorCodes('&', noColor);
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.YELLOW;
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public Command getCommand() {
		Command command = new Command(getName()) {
			@Override
			public boolean execute(CommandSender sender, String commandLabel, String[] args) {
				if (!testPermission(sender)) {
					return true;
				}
				
				if (!(sender instanceof Player)) {
					return true;
				}

				Player player = (Player) sender;

				ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

				chatPlayer.setFocused(DefaultLocalChatChannel.this);

				player.sendMessage(String.format(Constants.MESSAGE_FOCUSED_CHANNEL_SET_TO, "local"));

				return true;
			}
		};

		command.setLabel(getName());
		command.setUsage("/local");
		command.setDescription("Set your focused chat channel to local");
		command.setPermissionMessage(null);

		return command;
	}

	@Override
	public boolean isLogged() {
		return true;
	}
	
	@Override
	public boolean isLoggedGlobally() {
		return false;
	}
	
	@Override
	public boolean muteable() {
		return true;
	}

	@Override
	public void sendToChat(GenericChatMessage chatMessage) {
		if (ChatPlugin.getInstance().getServerName() == null) {
			return;
		}
		
		if (!ChatPlugin.getInstance().getServerName().equals(chatMessage.getServerSentFrom())) {
			return;
		}
		
		String message = chatMessage.getPayload();

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(onlinePlayer.getUniqueId());
			
			if (chatPlayer.isIgnoring(chatMessage.getSender())) {
				continue;
			}
			
			OfflinePlayer potentialOfflinePlayer = Bukkit.getOfflinePlayer(chatMessage.getSender());
			
			if (potentialOfflinePlayer != null && chatPlayer.isIgnoring(potentialOfflinePlayer.getUniqueId())) {
				continue;
			}
			
			// if no permission defined or they have it
			if (getPermission() == null || onlinePlayer.hasPermission(getPermission())) {
				String format = getFormat(onlinePlayer.getName(), chatMessage);

				format = format
						.replace("%prefix%", chatMessage.getSenderPrefix())
						.replace("%sender%", chatMessage.getSender());
				
				if (chatMessage.getSenderNickname() != null) {
					format = format.replace("%nickname%", chatMessage.getSenderNickname());
				}

				if (chatMessage.colorizePayload()) {
					format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', message));
				} else {
					format = format.replace("%message%", message);
				}

				onlinePlayer.sendMessage(format);
			}
		}

		logAsync(chatMessage);
	}

}
