package com.ruinscraft.chat.message;

public interface ChatMessage {

	String getSender();
	
	String getPayload();
	
	String getIntendedChannelName();
	
}
