package com.ruinscraft.chat;

import java.util.UUID;

public class ChatMessage {

	private UUID sender;
	private String payload;
	private long time;
	
	public ChatMessage() {}
	
	public ChatMessage(UUID sender, String payload) {
		this.sender = sender;
		this.payload = payload;
		this.time = System.currentTimeMillis();
	}

	public ChatMessage(UUID sender, String payload, long time) {
		this(sender, payload);
		this.time = time;
	}
	
	public void setSender(UUID sender) {
		this.sender = sender;
	}
	
	public UUID getSender() {
		return sender;
	}
	
	public void setPayload(String payload) {
		this.payload = payload;
	}
	
	public String getPayload() {
		return payload;
	}
	
	public void setInceptionTime(long time) {
		this.time = time;
	}
	
	public long getInceptionTime() {
		return time;
	}

	@Override
	public String toString() {
		return "ChatMessage [sender=" + sender + ", payload=" + payload + ", time=" + time + "]";
	}
	
}
