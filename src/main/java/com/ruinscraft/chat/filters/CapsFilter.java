package com.ruinscraft.chat.filters;

public class CapsFilter implements ChatFilter {

	public static final int MAX_CAPS_PCT = 60;
	
	@Override
	public String filter(String message) throws NotSendableException {
		int uppercaseLetters = 0;
		
		for (char c : message.toCharArray()) {
			if (Character.isUpperCase(c)) {
				uppercaseLetters++;
			}
		}
		
		double pct = (uppercaseLetters * 1D) / (message.length() * 1D) * 100D;
		
		if (pct > MAX_CAPS_PCT) {
			return message.toLowerCase();
		}
		
		return message;
	}
	
}
