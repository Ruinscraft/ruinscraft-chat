package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.message.ChatMessage;

import java.util.concurrent.Callable;

public interface ChatLogger extends AutoCloseable {

    Callable<Void> log(ChatMessage message);

    @Override
    default void close() {}

}
