package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class DefaultLocalChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "local";
	}
	
	@Override
	public String getFormat(String viewer, GenericChatMessage chatMessage) {
		return "[L] [%prefix%] %sender% >" + getMessageColor() + " %message%";
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
		return null;
	}

	@Override
	public boolean isLogged() {
		return true;
	}

}
