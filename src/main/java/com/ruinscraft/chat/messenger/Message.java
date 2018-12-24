package com.ruinscraft.chat.messenger;

import java.util.UUID;

import com.google.gson.Gson;

public class Message {

	private static final Gson GSON = new Gson();

	private UUID messageId;
	private long inceptionTime;
	private String payloadClass;
	private String payload;

	public Message() {
		messageId = UUID.randomUUID();
		inceptionTime = System.currentTimeMillis();
	}

	public Message(Object payload) {
		this();
		this.payloadClass = payload.getClass().getName();
		this.payload = GSON.toJson(payload);
	}

	public Message(UUID messageId, long inceptionTime, Object payload) {
		this.messageId = messageId;
		this.inceptionTime = inceptionTime;
		this.payload = GSON.toJson(payload);
	}

	public UUID getMessageId() {
		return messageId;
	}

	public long getInceptionTime() {
		return inceptionTime;
	}

	public String getPayloadClass() {
		return payloadClass;
	}

	public String getPayload() {
		return payload;
	}

	public String serialize() {
		return GSON.toJson(this);
	}

}
