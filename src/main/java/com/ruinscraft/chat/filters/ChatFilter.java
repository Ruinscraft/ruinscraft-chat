package com.ruinscraft.chat.filters;

public interface ChatFilter {

	String filter(String message) throws NotSendableException;
	
}
