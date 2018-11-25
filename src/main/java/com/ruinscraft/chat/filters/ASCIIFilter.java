package com.ruinscraft.chat.filters;

import com.google.common.base.CharMatcher;

public class ASCIIFilter implements ChatFilter {

	@Override
	public String filter(String message) throws NotSendableException {
		if (!CharMatcher.ascii().matchesAllOf(message)) {
			throw new NotSendableException("Message must be ASCII only");
		}
		
		return message;
	}
	
}
