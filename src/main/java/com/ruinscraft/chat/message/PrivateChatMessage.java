package com.ruinscraft.chat.message;

public class PrivateChatMessage extends GenericChatMessage {

	private final String recipient;
		public PrivateChatMessage(String sender, String recipient, String intendedChannelName, String payload) {
		super(sender, intendedChannelName, payload);
		this.recipient = recipient;
	}
	
	public String getRecipient() {
		return recipient;
	}

}
