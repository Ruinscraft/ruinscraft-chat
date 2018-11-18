package com.ruinscraft.chat.channel;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.ChatMessage;

public class GlobalChatChannel implements ChatChannel {

	@Override
	public String getName() {
		return "global";
	}

	@Override
	public String getFormat(String context) {
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

	@Override
	public boolean isOneToOne() {
		return false;
	}

	@Override
	public boolean isLogged() {
		return true;
	}

	@Override
	public void send(ChatMessage message) {

	}

}
