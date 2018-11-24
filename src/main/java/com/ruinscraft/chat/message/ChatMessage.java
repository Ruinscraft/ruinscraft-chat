package com.ruinscraft.chat.message;

public interface ChatMessage {

	String getSenderPrefix();
	
	String getSender();
	
	String getServerSentFrom();
	
	String getIntendedChannelName();
	
	boolean colorizePayload();
	
	String getPayload();
	
}
