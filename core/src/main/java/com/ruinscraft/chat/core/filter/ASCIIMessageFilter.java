package com.ruinscraft.chat.core.filter;

import java.util.Optional;

public class ASCIIMessageFilter extends MessageFilter {

    public ASCIIMessageFilter() {
        super("ASCII Filter", message -> {
            boolean ascii = message.chars().allMatch(c -> c < 128);

            if (ascii) {
                return Optional.of(message);
            } else {
                return Optional.empty();
            }
        });
    }

}
