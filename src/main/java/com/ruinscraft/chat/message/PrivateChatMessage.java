package com.ruinscraft.chat.message;

import java.util.UUID;

public class PrivateChatMessage extends GenericChatMessage {

	private final String recipient;

	public PrivateChatMessage(String senderPrefix, String senderNickname, UUID uuid, String sender, String recipient, String serverSentFrom, String intendedChannelName, boolean colorizePayload, String payload) {
		super(senderPrefix, senderNickname, uuid, sender, serverSentFrom, intendedChannelName, colorizePayload, payload);
		this.recipient = recipient;
	}

	public String getRecipient() {
		return recipient;
	}

}
