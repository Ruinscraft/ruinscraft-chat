package com.ruinscraft.chat.message;

public class GenericChatMessage implements ChatMessage {

	private long time;
	private String sender;
	private String payload;
	
	public GenericChatMessage(long time, String sender, String payload) {
		this.time = time;
		this.sender = sender;
		this.payload = payload;
	}
	
	@Override
	public long getTimeSent() {
		return time;
	}

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getPayload() {
		return payload;
	}
	
}
