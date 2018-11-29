package com.ruinscraft.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.ruinscraft.chat.Constants;

public class ClearChatCommand implements CommandExecutor {

	// TODO: add permission in plugin.yml
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		for (int i = 0; i < 100; i++) {
			Bukkit.broadcastMessage("");
		}
		Bukkit.broadcastMessage(Constants.COLOR_BASE + "Chat has been cleared");
		return true;
	}
	
}
