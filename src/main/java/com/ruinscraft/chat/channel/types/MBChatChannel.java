package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class MBChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "mb";
	}

	@Override
	public String getFormat(GenericChatMessage context) {
		return "";
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.GREEN;
	}

	@Override
	public String getPermission() {
		return "ruinscraft.chat.channel.mb";
	}

	@Override
	public String[] getCommands() {
		return new String[] {"mb"};
	}

	@Override
	public boolean isLogged() {
		return false;
	}
	
}
