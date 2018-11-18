package com.ruinscraft.chat.messenger;

public interface MessageDispatcher extends AutoCloseable {

	void dispatch(Message message);
	
	@Override
	default void close() {}
	
}
