package com.ruinscraft.chat.players.storage;

import java.sql.Connection;

public interface SQLChatPlayerStorage extends ChatPlayerStorage {
	
	Connection getConnection();
	
}
