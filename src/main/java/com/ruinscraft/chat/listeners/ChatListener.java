package com.ruinscraft.chat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
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

		ChatChannel<GenericChatMessage> chatChannel = chatPlayer.getFocused();
		GenericChatMessage chatMessage = new GenericChatMessage(player.getName(), chatChannel.getName(), payload);

		chatChannel.dispatch(chatPlugin.getMessageManager().getDispatcher(), player, chatMessage);
	}

}
