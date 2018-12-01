package com.ruinscraft.chat.logging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public class MySQLChatLogger implements ChatLogger {

	private static final String SQL_LOGGER_TABLE_NAME = "ruinscraft_chat_logs";
	private static final String SQL_CREATE_CHAT_LOG_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (sender VARCHAR(36), recipient VARCHAR(36) DEFAULT NULL, time BIGINT, channel VARCHAR(16), payload VARCHAR(256));", SQL_LOGGER_TABLE_NAME);
	private static final String SQL_INSERT_LOG = String.format("INSERT INTO %s (sender, recipient, time, channel, payload) VALUES (?, ?, ?, ?, ?);", SQL_LOGGER_TABLE_NAME);

	private Connection connection;
	private final String address;
	private final int port;
	private final String database;
	private final String username;
	private final char[] password;

	public MySQLChatLogger(String address, int port, String database, String username, String password) {
		this.address = address;
		this.port = port;
		this.database = database;
		this.username = username;
		this.password = password.toCharArray();

		Connection connection = getConnection();

		boolean error = false;

		try {
			if (connection.isClosed()) {
				ChatPlugin.warning("Chat logging storage MySQL connection lost");
				error = true;
			} else {
				ChatPlugin.info("Chat logging storage MySQL connection established");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			error = true;
		}

		if (error) return;

		try (PreparedStatement create = getConnection().prepareStatement(SQL_CREATE_CHAT_LOG_TABLE)) {
			create.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Callable<Void> log(ChatMessage message) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (PreparedStatement insert = getConnection().prepareStatement(SQL_INSERT_LOG)) {
					insert.setString(1, message.getSender());
					if (message instanceof PrivateChatMessage) {
						PrivateChatMessage pm = (PrivateChatMessage) message;
						insert.setString(2, pm.getRecipient());
					} else {
						insert.setString(2, null);
					}
					insert.setLong(3, System.currentTimeMillis());
					insert.setString(4, message.getIntendedChannelName());
					insert.setString(5, message.getPayload());
					insert.execute();
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
