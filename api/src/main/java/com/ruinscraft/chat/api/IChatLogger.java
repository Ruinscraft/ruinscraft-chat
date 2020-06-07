package com.ruinscraft.chat.api;

public interface IChatLogger {

    IMessageFormatter getFormatter();

    void log(IChatMessage message);

}
