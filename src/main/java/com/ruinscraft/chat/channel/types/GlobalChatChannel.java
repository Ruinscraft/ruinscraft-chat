package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class GlobalChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "global";
	}

	@Override
	public String getFormat(String viewer, GenericChatMessage context) {
		ChatColor globalColor = ChatColor.GRAY;
		String currentServer = ChatPlugin.getInstance().getServerName();
		
		if (context.getServerSentFrom().equals(currentServer)) {
			globalColor = ChatColor.GREEN;
		}
		
		String noColor = globalColor + "[G] &7[%prefix%&7] %sender% &8&l>&r" + getMessageColor() + " %message%";
		return ChatColor.translateAlternateColorCodes('&', noColor);
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.WHITE;
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
				
				chatPlayer.setFocused(GlobalChatChannel.this);

				player.sendMessage("Focused channel set to global");
				
				return true;
			}
		};
		
		command.setLabel(getName());
		command.setUsage("/global");
		command.setDescription("Set your focused chat channel to global");
		command.setPermissionMessage(null);
		
		return command;
	}

	public boolean isLogged() {
		return true;
	}

}
