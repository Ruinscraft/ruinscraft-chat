package com.ruinscraft.chat.messenger;

import java.util.UUID;

public class Message {

	private UUID messageId;
	private long inceptionTime;
	
	public Message() {
		messageId = UUID.randomUUID();
		inceptionTime = System.currentTimeMillis();
	}

	public UUID getMessageId() {
		return messageId;
	}
	
	public long getInceptionTime() {
		return inceptionTime;
	}
	
}
