package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IMessageFormatter;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class DefaultMessageFormatter implements IMessageFormatter {

    private Set<Function<String, String>> replacements;

    public DefaultMessageFormatter() {
        replacements = new HashSet<>();

        replacements.add(message -> message.replace("%gamemode%", ""));
        replacements.add(message -> message.replace("%sender%", ""));
        replacements.add(message -> message.replace("%message%", ""));
    }

    @Override
    public Set<Function<String, String>> getReplacements() {
        return replacements;
    }

    @Override
    public String format(String message) {
        replacements.forEach(f -> f.apply(message));
        return message;
    }

}
