package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public class ConsoleChatLogger implements ChatLogger {

	private static final String PREFIX = "[CHAT] ";
	
	@Override
	public void log(ChatMessage message) {
		ChatChannel<?> chatChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(message.getIntendedChannelName());
		
		if (!chatChannel.isLoggedGlobally()) {
			String currentServer = ChatPlugin.getInstance().getServerName();
			
			if (!message.getServerSentFrom().equals(currentServer)) {
				return;
			}
		}
		
		if (message instanceof PrivateChatMessage) {
			PrivateChatMessage pm = (PrivateChatMessage) message;
			String format = "[%s -> %s] %s";
			System.out.println(PREFIX + String.format(format, pm.getSender(), pm.getRecipient(), pm.getPayload()));
		} else {
			String format = "[%s] [%s] [%s] > %s";
			System.out.println(PREFIX + String.format(format, message.getServerSentFrom(), message.getIntendedChannelName(), message.getSender(), message.getPayload()));
		}
	}
	
}
