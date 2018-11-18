package com.ruinscraft.chat.players.storage;

import com.ruinscraft.chat.players.ChatPlayer;

public interface PlayerStorage {

	void loadChatPlayer(ChatPlayer chatPlayer);
	
	void saveChatPlayer(ChatPlayer chatPlayer);
	
}
