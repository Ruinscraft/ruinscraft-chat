package com.ruinscraft.chat.filters;

public class LengthFilter implements ChatFilter {

	public static final int MAX_MESSAGE_LEN = 256;

	@Override
	public String filter(String message) throws NotSendableException {
		if (message.length() > MAX_MESSAGE_LEN) throw new NotSendableException("Message to long");
		return message;
	}

}
