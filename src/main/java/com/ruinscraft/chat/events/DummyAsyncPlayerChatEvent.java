package com.ruinscraft.chat.events;

import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.collect.Sets;

public class DummyAsyncPlayerChatEvent extends AsyncPlayerChatEvent {

	public DummyAsyncPlayerChatEvent(boolean async, Player who, String message) {
		super(async, who, message, Sets.newHashSet(who));
	}

}
