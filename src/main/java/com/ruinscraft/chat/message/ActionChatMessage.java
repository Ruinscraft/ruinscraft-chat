package com.ruinscraft.chat.message;

import java.util.UUID;

public class ActionChatMessage extends GenericChatMessage {

	public ActionChatMessage(String senderPrefix, String senderNickname, UUID uuid, String sender,
			String serverSentFrom, String intendedChannelName, boolean colorizePayload, String payload) {
		super(senderPrefix, senderNickname, uuid, sender, serverSentFrom, intendedChannelName, colorizePayload, payload);
	}
	
}
