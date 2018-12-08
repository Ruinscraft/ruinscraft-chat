package com.ruinscraft.chat.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.events.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class ChatListener implements Listener {

	private static ChatPlugin chatPlugin = ChatPlugin.getInstance();

	@EventHandler
	public void onAsyncChat(AsyncPlayerChatEvent event) {
		if (event instanceof DummyAsyncPlayerChatEvent) {
			return;
		}

		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		String payload = event.getMessage();

		if (payload.isEmpty()) {
			return;
		}

		event.setCancelled(true);

		ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getChatPlayer(player.getUniqueId());
		ChatChannel<GenericChatMessage> chatChannel = chatPlayer.getFocused();
		
		if (chatChannel.getPermission() != null && !player.hasPermission(chatChannel.getPermission())) {
			chatPlayer.setFocused(chatPlugin.getChatChannelManager().getDefaultChatChannel());
		}
		
		String senderPrefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
		String nickname = chatPlayer.getNickname();
		boolean allowColor = player.hasPermission(Constants.PERMISSION_COLORIZE_MESSAGES);
		GenericChatMessage chatMessage = new GenericChatMessage(senderPrefix, nickname, player.getUniqueId(), player.getName(), chatPlugin.getServerName(), chatChannel.getName(), allowColor, payload);

		try {
			/* Safe because this is already async */
			chatChannel.dispatch(player, chatMessage, true).call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
