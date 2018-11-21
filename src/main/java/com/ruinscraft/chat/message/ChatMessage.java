package com.ruinscraft.chat.message;

public interface ChatMessage {

	long getTimeSent();
	
	String getSender();
	
	String getPayload();
	
}
