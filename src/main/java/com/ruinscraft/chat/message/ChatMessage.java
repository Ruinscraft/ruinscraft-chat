package com.ruinscraft.chat.message;

import java.util.UUID;

public interface ChatMessage {

	String getSenderPrefix();

	String getSenderNickname();

	UUID getSenderUUID();

	String getSender();

	String getServerSentFrom();

	String getIntendedChannelName();

	boolean colorizePayload();

	void setPayload(String payload);

	String getPayload();

}
