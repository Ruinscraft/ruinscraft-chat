package com.ruinscraft.chat.messenger;

public abstract class MessageManager implements AutoCloseable {

	private MessageConsumer consumer;
	private MessageDispatcher dispatcher;
	
	public MessageManager() {
		
	}
	
	public MessageConsumer getConsumer() {
		return consumer;
	}
	
	public MessageDispatcher getDispatcher() {
		return dispatcher;
	}

}
