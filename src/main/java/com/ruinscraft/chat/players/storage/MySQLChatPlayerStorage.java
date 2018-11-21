package com.ruinscraft.chat.players.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.ruinscraft.chat.players.ChatPlayer;

public class MySQLChatPlayerStorage implements SQLChatPlayerStorage {

	private Connection connection;
	
	private final String address;
	private final int port;
	private final String database;
	private final String username;
	private final char[] password;

	public MySQLChatPlayerStorage(String address, int port, String database, String username, char[] password) {
		this.address = address;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public void loadChatPlayer(ChatPlayer chatPlayer) {
		System.out.println("mysql loading chat player");
	}

	@Override
	public void saveChatPlayer(ChatPlayer chatPlayer) {
		System.out.println("mysql saving chat player");
	}

	@Override
	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			if (connection == null || connection.isClosed()) {
				connection = DriverManager.getConnection(
						String.format("jdbc:mysql://%s:%d/%s", address, port, database),
						username,
						new String(password));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return connection;
	}
	
}
