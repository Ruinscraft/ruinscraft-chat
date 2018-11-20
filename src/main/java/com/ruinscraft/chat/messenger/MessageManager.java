package com.ruinscraft.chat.messenger;

public interface MessageManager extends AutoCloseable {

	MessageConsumer getConsumer();
	
	MessageDispatcher getDispatcher();

}
