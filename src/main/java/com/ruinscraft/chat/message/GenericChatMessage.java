package com.ruinscraft.chat.message;

public class GenericChatMessage implements ChatMessage {

	private final String senderPrefix;
	private final String sender;
	private final String serverSentFrom;
	private final String intendedChannelName;
	private final boolean colorizePayload;
	private final String payload;
	
	public GenericChatMessage(String senderPrefix, String sender, String serverSentFrom, String intendedChannelName, boolean colorizePayload, String payload) {
		this.senderPrefix = senderPrefix;
		this.sender = sender;
		this.serverSentFrom = serverSentFrom;
		this.intendedChannelName = intendedChannelName;
		this.colorizePayload = colorizePayload;
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
	public String getServerSentFrom() {
		return serverSentFrom;
	}
	
	@Override
	public boolean colorizePayload() {
		return colorizePayload;
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
