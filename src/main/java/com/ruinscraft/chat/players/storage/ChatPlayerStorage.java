package com.ruinscraft.chat.players.storage;

import java.util.concurrent.Callable;

public interface ChatPlayerStorage extends AutoCloseable {

	Callable<Void> loadChatPlayer(MutableChatPlayer chatPlayer);
	
	Callable<Void> saveChatPlayer(MutableChatPlayer chatPlayer);
	
	@Override
	default void close() {}
	
}
