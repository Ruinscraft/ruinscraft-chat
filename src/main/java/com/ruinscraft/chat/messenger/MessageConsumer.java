package com.ruinscraft.chat.messenger;

public interface MessageConsumer extends AutoCloseable {

	void consume(Message message);
	
	@Override
	default void close() {}
	
}
