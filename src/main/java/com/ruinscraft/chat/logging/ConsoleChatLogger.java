package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public class ConsoleChatLogger implements ChatLogger {

	private static final String PREFIX = "[CHAT] ";
	
	@Override
	public void log(ChatMessage message) {
		if (message instanceof PrivateChatMessage) {
			PrivateChatMessage pm = (PrivateChatMessage) message;
			System.out.println(PREFIX + "[" + pm.getSender() + " -> " + pm.getRecipient() + "] " + pm.getPayload());
		} else {
			// Don't log to console if not global and not the same server
			if (!message.getIntendedChannelName().equals("global")) {
				if (!message.getServerSentFrom().equals(ChatPlugin.getInstance().getServerName())) {
					return;
				}
			}
			
			System.out.println(PREFIX + "[" + message.getSender() + "] " + "[" + message.getServerSentFrom() + "] " + message.getPayload());
		}
	}
	
}
