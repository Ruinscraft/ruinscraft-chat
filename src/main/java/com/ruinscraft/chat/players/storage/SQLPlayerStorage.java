package com.ruinscraft.chat.players.storage;

import java.sql.Connection;

public interface SQLPlayerStorage extends PlayerStorage, AutoCloseable {
	
	Connection getConnection();
	
}
