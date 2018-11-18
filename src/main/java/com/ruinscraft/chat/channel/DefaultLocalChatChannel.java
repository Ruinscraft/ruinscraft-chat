package com.ruinscraft.chat.channel;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.ChatMessage;

public class DefaultLocalChatChannel implements ChatChannel {

	@Override
	public String getName() {
		return "local";
	}
	
	@Override
	public String getFormat(String context) {
		return "[L] [%prefix%] %player% > " + getMessageColor() + " %message%";
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
	public String[] getCommands() {
		return new String[] {"local"};
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
