package com.ruinscraft.chat.message;

public class PrivateChatMessage extends GenericChatMessage {

	private String recipient;
		public PrivateChatMessage(long time, String sender, String recipient, String payload) {
		super(time, sender, payload);
		this.recipient = recipient;
	}
	
	public String getRecipient() {
		return recipient;
	}

}
