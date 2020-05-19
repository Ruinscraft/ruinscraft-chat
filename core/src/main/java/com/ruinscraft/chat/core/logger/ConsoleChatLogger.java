package com.ruinscraft.chat.core.logger;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatMessage;

import java.io.PrintStream;

public class ConsoleChatLogger extends ChatLogger {

    private static final PrintStream STREAM = System.out;

    public ConsoleChatLogger(IChat chat) {
        super(chat);
    }

    @Override
    public void log(IChatMessage message) {
        // TODO: get format
    }

}
