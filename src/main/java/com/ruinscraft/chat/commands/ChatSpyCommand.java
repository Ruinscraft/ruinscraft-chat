package com.ruinscraft.chat.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *	A command to allow privileged Players to "spy" on certain chat channels.
 *	Brings up a GUI much like the ChatCommand to enable/disable spying on channels.
 */
public class ChatSpyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		
		return true;
	}
	
}
