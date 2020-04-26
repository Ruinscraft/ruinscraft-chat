package com.ruinscraft.chat.core.filter;

import java.util.Optional;

public class CapitalsMessageFilter extends MessageFilter {

    public static final int MIN_CAPS_LETTERS_BEFORE_CHECKING = 32;
    public static final int MAX_CAPS_PCT = 60;

    public CapitalsMessageFilter() {
        super("Capitals Filter", message -> {
            int uppercaseLetters = 0;

            for (char c : message.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    uppercaseLetters++;
                }
            }

            if (uppercaseLetters < MIN_CAPS_LETTERS_BEFORE_CHECKING) {
                return Optional.of(message);
            } else {
                double pct = (uppercaseLetters * 1D) / (message.length() * 1D) * 100D;

                if (pct > MAX_CAPS_PCT) {
                    return Optional.of(message.toLowerCase());
                }
            }

            return Optional.of(message);
        });
    }

}
