package com.ruinscraft.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.players.ChatPlayer;

import net.md_5.bungee.api.ChatColor;

/**
 *	A command for Players set a nickname (for use in instanceof DefaultLocalChatChannel).
 */
public class NicknameCommand implements CommandExecutor {

	public static final int MAX_LENGTH = 24;
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player player = (Player) sender;
		ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
		
		if (args.length < 1) {
			if (chatPlayer.hasNickname()) {
				player.sendMessage(Constants.COLOR_BASE + "Your current nickname is " + Constants.COLOR_ACCENT + chatPlayer.getNickname());
				player.sendMessage(Constants.COLOR_BASE + "You can reset it with /nicknamereset");
				return true;
			} else {
				player.sendMessage(Constants.COLOR_BASE + "You currently do not have a nickname");
				player.sendMessage(Constants.COLOR_BASE + "You can set one with " + Constants.COLOR_ACCENT + "/" + label + " <name>");
				return true;
			}
		}
		
		String desiredNickname = args[0];
		
		if (desiredNickname == null) {
			player.sendMessage(ChatColor.RED + "Nickname not valid.");
			return true;
		}
		
		if (desiredNickname.isEmpty()) {
			player.sendMessage(ChatColor.RED + "Nickname not valid.");
			return true;
		}
		
		if (desiredNickname.length() > MAX_LENGTH) {
			player.sendMessage(ChatColor.RED + "Nickname too long (max of 24 characters).");
			return true;
		}

		for (ChatFilter chatFilter : ChatPlugin.getInstance().getChatFilterManager().getChatFilters()) {
			try {
				desiredNickname = chatFilter.filter(desiredNickname);
			} catch (NotSendableException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
				return true;
			}
		}
		
		chatPlayer.setNickname(desiredNickname);
		player.sendMessage(Constants.COLOR_BASE + "Nickname set to " + Constants.COLOR_ACCENT + desiredNickname);
		
		return true;
	}
	
}
