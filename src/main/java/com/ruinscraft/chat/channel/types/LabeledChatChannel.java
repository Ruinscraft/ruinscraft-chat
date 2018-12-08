package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;

public abstract class LabeledChatChannel<T extends ChatMessage> extends ChatChannel<T> {

	public LabeledChatChannel(String name, String prettyName, String permission, ChatColor messageColor, boolean logged,
			boolean mutable, boolean spyable) {
		super(name, prettyName, permission, messageColor, logged, mutable, spyable);
	}
 
	public abstract String getLabel(GenericChatMessage context);
	
}
