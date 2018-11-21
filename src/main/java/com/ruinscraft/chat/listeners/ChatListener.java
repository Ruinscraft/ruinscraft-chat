package com.ruinscraft.chat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class ChatListener implements Listener {

	private static ChatPlugin chatPlugin = ChatPlugin.getInstance();

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String payload = event.getMessage();

		if (payload.isEmpty()) {
			return;
		}

		event.setCancelled(true);

		ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getChatPlayer(player.getUniqueId());

		if (chatPlayer == null) {
			player.sendMessage("Failed to send message");
			return;
		}

		GenericChatMessage chatMessage = new GenericChatMessage(System.currentTimeMillis(), player.getName(), payload);

		chatPlayer.getFocused().send(chatMessage);
	}

}
