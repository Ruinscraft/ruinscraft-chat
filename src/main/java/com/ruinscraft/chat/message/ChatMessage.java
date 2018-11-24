package com.ruinscraft.chat.message;

public interface ChatMessage {

	String getSenderPrefix();
	
	String getSender();
	
	String getPayload();
	
	String getIntendedChannelName();
	
}
