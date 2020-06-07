package com.ruinscraft.chat.api;

public interface IMessageFormatter {

    String getFormat();

    String format(IChatMessage input, Object... replacements);

}
