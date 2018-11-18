package com.ruinscraft.chat.players;

import java.util.Set;
import java.util.UUID;

import com.ruinscraft.chat.channel.ChatChannel;

public class ChatPlayer {

	private UUID mojangUUID;
	private Set<UUID> ignoring;
	private ChatChannel focused;
	// set hidden channels
	// focused channel, may not be in hidden channels

	public ChatChannel getFocused() {
		return focused;
	}
	
}
