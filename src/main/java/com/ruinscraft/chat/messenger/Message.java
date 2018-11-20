package com.ruinscraft.chat.messenger;

import java.util.UUID;

import com.google.gson.Gson;

public class Message {

	private static final Gson GSON = new Gson();
	
	private UUID messageId;
	private long inceptionTime;
	private Object payload;
	
	public Message() {
		messageId = UUID.randomUUID();
		inceptionTime = System.currentTimeMillis();
	}
	
	public Message(Object payload) {
		this();
		this.payload = payload;
	}
	
	public Message(UUID messageId, long inceptionTime, Object payload) {
		this.messageId = messageId;
		this.inceptionTime = inceptionTime;
		this.payload = payload;
	}

	public UUID getMessageId() {
		return messageId;
	}
	
	public long getInceptionTime() {
		return inceptionTime;
	}
	
	public Object getPayload() {
		return payload;
	}
	
	public String serialize() {
		return GSON.toJson(this);
	}
	
}
