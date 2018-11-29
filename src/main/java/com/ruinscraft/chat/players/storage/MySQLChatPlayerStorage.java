package com.ruinscraft.chat.players.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;
import com.ruinscraft.chat.players.MinecraftIdentity;

public class MySQLChatPlayerStorage implements SQLChatPlayerStorage {

	/* PLAYER TABLE */
	private static final String SQL_CREATE_PLAYERS = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT AUTO_INCREMENT, mojang_uuid VARCHAR(36), nickname VARCHAR(24), focused VARCHAR(16), PRIMARY KEY (chat_player_id), UNIQUE (mojang_uuid));", Table.PLAYERS);
	private static final String SQL_SELECT_PLAYER_BY_UUID = String.format("SELECT * FROM %s WHERE mojang_uuid = ?;", Table.PLAYERS);
	private static final String SQL_UPDATE_PLAYERS = String.format("UPDATE %s SET mojang_uuid = ?, nickname = ?, focused = ? WHERE chat_player_id = ?;", Table.PLAYERS);
	private static final String SQL_INSERT_PLAYERS = String.format("INSERT INTO %s (mojang_uuid, nickname, focused) VALUES (?, ?, ?);", Table.PLAYERS);

	/* IGNORING TABLE */
	private static final String SQL_CREATE_IGNORING = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, minecraft_identity VARCHAR(36), UNIQUE KEY (chat_player_id, minecraft_identity), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.IGNORING, Table.PLAYERS);
	private static final String SQL_SELECT_IGNORING = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.IGNORING);
	private static final String SQL_INSERT_IGNORING = String.format("INSERT INTO %s (chat_player_id, minecraft_identity) VALUES (?, ?);", Table.IGNORING);
	private static final String SQL_DELETE_IGNORING = String.format("DELETE FROM %s WHERE chat_player_id = ? AND minecraft_identity = ?;", Table.IGNORING);

	/* MUTES TABLE */
	private static final String SQL_CREATE_MUTED = String.format("CREATE TABLE IF NOT EXISTS %s (chat_player_id INT, channel_name VARCHAR(16), UNIQUE KEY (chat_player_id, channel_name), FOREIGN KEY (chat_player_id) REFERENCES %s (chat_player_id));", Table.MUTED, Table.PLAYERS);
	private static final String SQL_SELECT_MUTED = String.format("SELECT * FROM %s WHERE chat_player_id = ?;", Table.MUTED);
	private static final String SQL_INSERT_MUTED = String.format("INSERT INTO %s (chat_player_id, channel_name) VALUES (?, ?);", Table.MUTED);
	private static final String SQL_DELETE_MUTED = String.format("DELETE FROM %s WHERE chat_player_id = ? AND channel_name = ?;", Table.MUTED);

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

		Connection connection = getConnection();

		boolean error = false;

		try {
			if (connection.isClosed()) {
				ChatPlugin.warning("MySQL connection lost");
				error = true;
			} else {
				ChatPlugin.info("MySQL connection established");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			error = true;
		}

		if (error) return;

		/* CREATE PLAYERS TABLE */
		try (PreparedStatement create = getConnection().prepareStatement(SQL_CREATE_PLAYERS)) {
			create.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/* CREATE IGNORING TABLE */
		try (PreparedStatement create = getConnection().prepareStatement(SQL_CREATE_IGNORING)) {
			System.out.println(SQL_CREATE_IGNORING);
			create.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		/* CREATE MUTES TABLE */
		try (PreparedStatement create = getConnection().prepareStatement(SQL_CREATE_MUTED)) {
			System.out.println(SQL_CREATE_MUTED);
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
				/* SELECT FROM PLAYERS TABLE */
				try (PreparedStatement select = getConnection().prepareStatement(SQL_SELECT_PLAYER_BY_UUID)) {
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

				if (chatPlayer.getChatPlayerId() == 0) {
					return null;
				}

				/* SELECT FROM IGNORING TABLE */
				try (PreparedStatement select = getConnection().prepareStatement(SQL_SELECT_IGNORING)) {
					select.setInt(1, chatPlayer.getChatPlayerId());

					try (ResultSet rs = select.executeQuery()) {
						while (rs.next()) {
							String identityString = rs.getString("minecraft_identity");
							MinecraftIdentity minecraftIdentity = new MinecraftIdentity(identityString);

							chatPlayer.ignoring.add(minecraftIdentity);
						}
					}
				}

				/* SELECT FROM MUTED TABLE */
				try (PreparedStatement select = getConnection().prepareStatement(SQL_SELECT_MUTED)) {
					select.setInt(1, chatPlayer.getChatPlayerId());

					try (ResultSet rs = select.executeQuery()) {
						while (rs.next()) {
							String channelName = rs.getString("channel_name");
							ChatChannel<? extends ChatMessage> chatChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(channelName);

							chatPlayer.muted.add(chatChannel);
						}
					}
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
					try (PreparedStatement insert = getConnection().prepareStatement(SQL_INSERT_PLAYERS, Statement.RETURN_GENERATED_KEYS)) {
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
					/* UPDATE PLAYERS TABLE */
					try (PreparedStatement update = getConnection().prepareStatement(SQL_UPDATE_PLAYERS)) {
						update.setString(1, chatPlayer.getMojangUUID().toString());
						update.setString(2, chatPlayer.getNickname());
						update.setString(3, chatPlayer.getFocused().getName());
						update.setInt(4, chatPlayer.getChatPlayerId());

						update.execute();
					} catch (SQLException e) {
						e.printStackTrace();
					}

					/* UPDATE IGNORING TABLE */
					Set<String> currentIgnoring = chatPlayer.ignoring.stream().map(MinecraftIdentity::getIdentity).collect(Collectors.toSet());
					Set<String> previousIgnoring = new HashSet<>();

					try (PreparedStatement select = getConnection().prepareStatement(SQL_SELECT_IGNORING)) {
						select.setInt(1, chatPlayer.getChatPlayerId());

						try (ResultSet rs = select.executeQuery()) {
							while (rs.next()) {
								String raw = rs.getString("minecraft_identity");
								previousIgnoring.add(raw);
							}
						}
					}

					SetView<String> toInsert = Sets.difference(currentIgnoring, previousIgnoring);
					SetView<String> toDelete = Sets.difference(previousIgnoring, currentIgnoring);

					for (String inserting : toInsert) {
						try (PreparedStatement insert = getConnection().prepareStatement(SQL_INSERT_IGNORING)) {
							insert.setInt(1, chatPlayer.getChatPlayerId());
							insert.setString(2, inserting);
							insert.execute();
						}
					}

					for (String deleting : toDelete) {
						try (PreparedStatement delete = getConnection().prepareStatement(SQL_DELETE_IGNORING)) {
							delete.setInt(1, chatPlayer.getChatPlayerId());
							delete.setString(2, deleting);
							delete.execute();
						}

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
