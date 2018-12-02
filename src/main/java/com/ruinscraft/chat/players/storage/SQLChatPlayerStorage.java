package com.ruinscraft.chat.players.storage;

import java.sql.Connection;

public interface SQLChatPlayerStorage extends ChatPlayerStorage {
	
	Connection getConnection();
	
	public final class Table {
		protected static final String PLAYERS = "ruinscraft_chat_players";
		protected static final String IGNORING = "ruinscraft_chat_ignoring";
		protected static final String MUTED = "ruinscraft_chat_muted";
		protected static final String SPYING = "ruinscraft_chat_spying";
	}
	
}
