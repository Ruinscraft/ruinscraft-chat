package com.ruinscraft.chat.players.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

import com.ruinscraft.chat.players.ChatPlayer;

public class MySQLChatPlayerStorage implements SQLChatPlayerStorage {

	private static final String PARENT_TABLE = "ruinscraft_chat_players";
	
	private static final String sql_create_table = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT AUTO_INCREMENT, mojang_uuid VARCHAR(36), nickname VARCHAR(24), focused VARCHAR(16), PRIMARY KEY (chat_player_id), UNIQUE (mojang_uuid));", PARENT_TABLE);
	private static final String sql_select_player_by_uuid = String.format("SELECT * FROM %s WHERE mojang_uuid = ?;", PARENT_TABLE);
	private static final String sql_update_player = String.format("UPDATE %s SET mojang_uuid = ?, nickname = ?, focused = ? WHERE chat_player_id = ?;", PARENT_TABLE);
	private static final String sql_insert_player = String.format("INSERT INTO %s (mojang_uuid, nickname, focused) VALUES (?, ?, ?);", PARENT_TABLE);
	
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
						while (rs.next()) {
							int chatPlayerId = rs.getInt("chat_player_id");
							String nickname = rs.getString("nickname");
							String focused = rs.getString("focused");
							
							chatPlayer.setChatPlayerId(chatPlayerId);
							chatPlayer.setNickname(nickname);
							chatPlayer.setFocused(focused);
						}
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
				/* Player not in database, insert */
				if (chatPlayer.getChatPlayerId() == 0) {
					try (PreparedStatement insert = getConnection().prepareStatement(sql_insert_player, Statement.RETURN_GENERATED_KEYS)) {
						insert.setString(1, chatPlayer.getMojangUUID().toString());
						insert.setString(2, chatPlayer.getNickname());
						insert.setString(3, chatPlayer.getFocused().getName());
						insert.execute();
						
						try (ResultSet rs = insert.getGeneratedKeys()) {
							while (rs.next()) {
								int chatPlayerId = rs.getInt(1);
								chatPlayer.setChatPlayerId(chatPlayerId);
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				
				/* Player already in database, update */
				else {
					try (PreparedStatement update = getConnection().prepareStatement(sql_update_player)) {
						update.setString(1, chatPlayer.getMojangUUID().toString());
						update.setString(2, chatPlayer.getNickname());
						update.setString(3, chatPlayer.getFocused().getName());
						update.setInt(4, chatPlayer.getChatPlayerId());
						
						update.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
