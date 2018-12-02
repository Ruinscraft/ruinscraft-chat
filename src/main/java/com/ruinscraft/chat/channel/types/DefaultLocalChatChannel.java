package com.ruinscraft.chat.channel.types;

import java.util.Arrays;

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

				if (commandLabel.equalsIgnoreCase("localcolor")) {
					if (!player.hasPermission("ruinscraft.command.localcolor")) {
						player.sendMessage(getPermissionMessage());
						return true;
					}

					if (args.length < 1) {
						if (!chatPlayer.hasMeta("localcolor")) {
							player.sendMessage(Constants.COLOR_BASE + "You currently do not have a local color set");
							player.sendMessage(Constants.COLOR_BASE + "Set one with /" + commandLabel + " <colorcode>");
							player.sendMessage(Constants.COLOR_BASE + "Color codes:");
							player.sendMessage(ChatColor.DARK_RED + "4 " + ChatColor.RED + "c " + ChatColor.GOLD + "6 " + ChatColor.YELLOW + "e " + ChatColor.DARK_GREEN + "2 " + ChatColor.GREEN + "a " + ChatColor.AQUA + "b " + ChatColor.DARK_AQUA + "3 " + ChatColor.DARK_BLUE + "1 " + ChatColor.BLUE + "9 " + ChatColor.LIGHT_PURPLE + "d " + ChatColor.DARK_PURPLE + "5 " + ChatColor.WHITE + "f " + ChatColor.GRAY + "7 " + ChatColor.DARK_GRAY + "8 " + ChatColor.BLACK + "0");
							return true;
						} else {
							char colorCode = chatPlayer.getMeta("localcolor").charAt(0);
							player.sendMessage(Constants.COLOR_BASE + "Your current local color is " + ChatColor.getByChar(colorCode) + ChatColor.getByChar(colorCode).name());
							player.sendMessage(Constants.COLOR_BASE + "You can reset it with /localcolorreset");
							return true;
						}
					}

					char newCode = args[0].charAt(0);
					
					ChatColor newChatColor = ChatColor.getByChar(newCode);
					
					if (newChatColor == null || !newChatColor.isColor()) {
						player.sendMessage(Constants.COLOR_ERROR + "Invalid color code");
						return true;
					}

					chatPlayer.setMeta("localcolor", Character.toString(newCode));
					player.sendMessage(Constants.COLOR_BASE + "Local color set to " + newChatColor + newChatColor.name());
					
					return true;
				}
				
				/* Requires no permission */
				else if (commandLabel.equalsIgnoreCase("localcolorreset")) {
					player.sendMessage(Constants.COLOR_BASE + "Local chat color reset");
					chatPlayer.setMeta("localcolor", null);
					return true;
				}
				
				chatPlayer.setFocused(DefaultLocalChatChannel.this);

				player.sendMessage(String.format(Constants.MESSAGE_FOCUSED_CHANNEL_SET_TO, "local"));

				return true;
			}
		};

		command.setAliases(Arrays.asList("localcolor", "localcolorreset"));
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
	public boolean spyable() {
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

		Player sender = Bukkit.getPlayer(chatMessage.getSender());
		
		if (sender == null || !sender.isOnline()) {
			return;
		}
		
		ChatPlayer senderChatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(sender.getUniqueId());

		ChatColor localColor = ChatColor.GRAY;
		
		if (senderChatPlayer.hasMeta("localcolor")) {
			localColor = ChatColor.getByChar(senderChatPlayer.getMeta("localcolor").charAt(0));
		}
		
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(onlinePlayer.getUniqueId());
			
			if (chatPlayer.isMuted(this)) {
				continue;
			}
			
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

				format = format.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', chatMessage.getSenderPrefix()));
				format = format.replace("%sender%", localColor + chatMessage.getSender());
				
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
