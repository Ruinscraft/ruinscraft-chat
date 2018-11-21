package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class MBHChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "mbh";
	}

	@Override
	public String getFormat(GenericChatMessage context) {
		return "";
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.LIGHT_PURPLE;
	}

	@Override
	public String getPermission() {
		return "ruinscraft.chat.channel.mbh";
	}

	@Override
	public String[] getCommands() {
		return new String[] {"mbh"};
	}

	@Override
	public boolean isLogged() {
		return false;
	}
	
}
