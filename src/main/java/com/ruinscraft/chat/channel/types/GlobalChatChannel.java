package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class GlobalChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "global";
	}

	@Override
	public String getFormat(GenericChatMessage context) {
		return "[G] [%prefix%] %player% > " + getMessageColor() + " %message%";
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
	public String[] getCommands() {
		return new String[] {"global"};
	}

	public boolean isLogged() {
		return true;
	}

}
