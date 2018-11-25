package com.ruinscraft.chat.message;

public interface ChatMessage {

	String getSenderPrefix();
	
	String getSenderNickname();
	
	String getSender();
	
	String getServerSentFrom();
	
	String getIntendedChannelName();
	
	boolean colorizePayload();
	
	String getPayload();
	
}
