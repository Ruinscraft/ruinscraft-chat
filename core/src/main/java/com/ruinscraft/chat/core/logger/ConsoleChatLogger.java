package com.ruinscraft.chat.core.logger;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IMessageFormatter;

import java.io.PrintStream;

public class ConsoleChatLogger extends ChatLogger {

    private static final PrintStream STREAM = System.out;

    private IMessageFormatter formatter;

    public ConsoleChatLogger(IChat chat) {
        super(chat);
        formatter = null;
    }

    @Override
    public IMessageFormatter getFormatter() {
        return formatter;
    }

    @Override
    public void log(IChatMessage message) {
        String format = formatter.format(message);
        STREAM.println(format);
    }

}
