package com.ruinscraft.chat.message;

public class PrivateChatMessage extends GenericChatMessage {

	private final String recipient;
	
	public PrivateChatMessage(String senderPrefix, String senderNickname, String sender, String recipient, String serverSentFrom, String intendedChannelName, boolean colorizePayload, String payload) {
		super(senderPrefix, senderNickname, sender, serverSentFrom, intendedChannelName, colorizePayload, payload);
		this.recipient = recipient;
	}
	
	public String getRecipient() {
		return recipient;
	}

}
