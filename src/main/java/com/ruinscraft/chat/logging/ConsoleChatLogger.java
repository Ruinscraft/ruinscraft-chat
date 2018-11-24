package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.message.ChatMessage;

public class ConsoleChatLogger implements ChatLogger {

	@Override
	public void log(ChatMessage message) {
		System.out.println(message.getSender() + ": " + message.getPayload());
	}
	
}
