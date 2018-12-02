package com.ruinscraft.chat.logging;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class ChatSpyLogger implements ChatLogger {

	private static final ChatColor SPY_COLOR = ChatColor.GRAY;
	private static final String PM_FORMAT 	= SPY_COLOR + "[%s -> %s] %s";
	private static final String GEN_FORMAT 	= SPY_COLOR + "[%s] [%s] [%s] %s";
	
	@Override
	public Callable<Void> log(ChatMessage message) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				ChatChannel<?> chatChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(message.getIntendedChannelName());
				
				if (chatChannel == null) {
					return null;
				}
				
				for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
					ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(onlinePlayer.getUniqueId());
					
					if (!chatPlayer.isSpying(chatChannel)) {
						continue;
					}
					
					if (message instanceof PrivateChatMessage) {
						PrivateChatMessage pm = (PrivateChatMessage) message;
						String send = String.format(PM_FORMAT, pm.getSender(), pm.getRecipient(), pm.getPayload());
						onlinePlayer.sendMessage(send);
					}
					
					else if (message instanceof GenericChatMessage) {
						String send = String.format(GEN_FORMAT, message.getServerSentFrom(), message.getIntendedChannelName(), message.getSender(), message.getPayload());
						onlinePlayer.sendMessage(send);
					}
				}
				return null;
			}
		};
	}
	
}
