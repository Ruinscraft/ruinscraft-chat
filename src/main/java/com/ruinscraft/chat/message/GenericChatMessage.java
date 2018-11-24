package com.ruinscraft.chat.message;

public class GenericChatMessage implements ChatMessage {

	private final String senderPrefix;
	private final String sender;
	private final String intendedChannelName;
	private final String payload;
	
	public GenericChatMessage(String senderPrefix, String sender, String intendedChannelName, String payload) {
		this.senderPrefix = senderPrefix;
		this.sender = sender;
		this.intendedChannelName = intendedChannelName;
		this.payload = payload;
	}
	
	@Override
	public String getSenderPrefix() {
		return senderPrefix;
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
