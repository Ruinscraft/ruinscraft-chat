package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class GlobalChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "global";
	}

	@Override
	public String getFormat(String viewer, GenericChatMessage context) {
		return "[G] [%prefix%] %sender% >" + getMessageColor() + " %message%";
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
		return null;
	}

	public boolean isLogged() {
		return true;
	}

}
