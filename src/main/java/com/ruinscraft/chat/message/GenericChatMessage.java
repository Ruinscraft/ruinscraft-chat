package com.ruinscraft.chat.message;

public class GenericChatMessage implements ChatMessage {

	private String sender;
	private String intendedChannelName;
	private String payload;
	
	public GenericChatMessage(String sender, String intendedChannelName, String payload) {
		this.sender = sender;
		this.intendedChannelName = intendedChannelName;
		this.payload = payload;
	}
	
	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getPayload() {
		return payload;
	}

	@Override
	public String getIntendedChannelName() {
		return intendedChannelName;
	}

}
