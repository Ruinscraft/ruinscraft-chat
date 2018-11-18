package com.ruinscraft.chat.players.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ruinscraft.chat.players.ChatPlayer;

public class MySQLPlayerStorage implements SQLPlayerStorage {

	private Connection connection;
	
	@Override
	public void loadChatPlayer(ChatPlayer chatPlayer) {
		try (PreparedStatement select = connection.prepareStatement("")) {
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveChatPlayer(ChatPlayer chatPlayer) {
		try (PreparedStatement update = connection.prepareStatement("")) {
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() throws Exception {
		connection.close();
	}

	@Override
	public Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(null, null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
}
