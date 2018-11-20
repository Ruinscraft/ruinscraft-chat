package com.ruinscraft.chat.players.storage;

import com.ruinscraft.chat.players.ChatPlayer;

public interface ChatPlayerStorage extends AutoCloseable {

	void loadChatPlayer(ChatPlayer chatPlayer);
	
	void saveChatPlayer(ChatPlayer chatPlayer);
	
	@Override
	default void close() {}
	
}
