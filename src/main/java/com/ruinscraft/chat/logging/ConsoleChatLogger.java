package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public class ConsoleChatLogger implements ChatLogger {

	@Override
	public void log(ChatMessage message) {
		if (message instanceof PrivateChatMessage) {
			PrivateChatMessage pm = (PrivateChatMessage) message;
			System.out.println("[" + pm.getSender() + " -> " + pm.getRecipient() + "] " + pm.getPayload());
		} else {
			System.out.println("[" + message.getSender() + "] " + "[" + message.getServerSentFrom() + "] " + message.getPayload());
		}
	}
	
}
