package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class MBSChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "mbs";
	}

	@Override
	public String getFormat(GenericChatMessage context) {
		return "";
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.GOLD;
	}

	@Override
	public String getPermission() {
		return "ruinscraft.chat.channel.mbs";
	}

	@Override
	public Command getCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLogged() {
		return false;
	}

}
