package com.ruinscraft.chat.message;

public interface ChatMessage {

	String getSenderPrefix();
	
	String getSender();
	
	boolean colorizePayload();
	
	String getPayload();
	
	String getIntendedChannelName();
	
}
