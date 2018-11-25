package com.ruinscraft.chat.players.storage;

import java.util.concurrent.Callable;

import com.ruinscraft.chat.players.ChatPlayer;

public interface ChatPlayerStorage extends AutoCloseable {

	Callable<Void> loadChatPlayer(ChatPlayer chatPlayer);
	
	Callable<Void> saveChatPlayer(ChatPlayer chatPlayer);
	
	@Override
	default void close() {}
	
}
