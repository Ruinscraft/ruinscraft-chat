package com.ruinscraft.chat.message;

public class PrivateChatMessage extends GenericChatMessage {

	private String recipient;
	
		super(time, sender, payload);
	}
	
	public String getRecipient() {
		return recipient;
	}

}