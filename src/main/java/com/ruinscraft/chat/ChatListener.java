package com.ruinscraft.chat;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String payload = event.getMessage();

		if (payload.isEmpty()) {
			return;
		}
		
		event.setCancelled(true);

		ChatMessage chatMessage = new ChatMessage(player.getUniqueId(), payload);
		
		
		
	}
	
}
