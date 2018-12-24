package com.ruinscraft.chat.logging;

import java.util.concurrent.Callable;

import com.ruinscraft.chat.message.ChatMessage;

public interface ChatLogger extends AutoCloseable {

	Callable<Void> log(ChatMessage message);

	@Override
	default void close() {}

}
