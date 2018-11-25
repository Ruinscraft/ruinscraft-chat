package com.ruinscraft.chat.players.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.ruinscraft.chat.players.ChatPlayer;

public class MySQLChatPlayerStorage implements SQLChatPlayerStorage {

	private static final String PARENT_TABLE = "ruinscraft_chat_players";
	
	private Connection connection;
	
	private final String address;
	private final int port;
	private final String database;
	private final String username;
	private final char[] password;

	private final String sql_create_table = String.format("CREATE TABLE IF NOT EXISTS %s ();", PARENT_TABLE);
	private final String sql_select_player_by_uuid = String.format("SELECT * FROM %s WHERE mojang_uuid = ?;", PARENT_TABLE);
	private final String sql_update_player = String.format("SELECT * FROM %s WHERE mojang_uuid = ?;", PARENT_TABLE);
	
	public MySQLChatPlayerStorage(String address, int port, String database, String username, char[] password) {
		this.address = address;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password;
		
		try (PreparedStatement create = getConnection().prepareStatement(sql_create_table)) {
			create.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Callable<Void> loadChatPlayer(ChatPlayer chatPlayer) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (PreparedStatement select = getConnection().prepareStatement(sql_select_player_by_uuid)) {
					select.setString(1, chatPlayer.getMojangUUID().toString());
					
					try (ResultSet rs = select.executeQuery()) {
						String focusedName = rs.getString("focused");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}};
	}

	@Override
	public Callable<Void> saveChatPlayer(ChatPlayer chatPlayer) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (PreparedStatement update = getConnection().prepareStatement("")) {
					
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};
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
						String.format("jdbc:mysql://%s:%d/%s?useSSL=false", address, port, database),
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
