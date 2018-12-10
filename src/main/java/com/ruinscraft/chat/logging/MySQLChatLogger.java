package com.ruinscraft.chat.logging;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.zaxxer.hikari.HikariDataSource;

public class MySQLChatLogger implements ChatLogger {

	private static final String SQL_LOGGER_TABLE_NAME = "ruinscraft_chat_logs";
	private static final String SQL_CREATE_CHAT_LOG_TABLE = String.format("CREATE TABLE IF NOT EXISTS %s (sender VARCHAR(36), recipient VARCHAR(36) DEFAULT NULL, time BIGINT, channel VARCHAR(16), payload VARCHAR(256));", SQL_LOGGER_TABLE_NAME);
	private static final String SQL_INSERT_LOG = String.format("INSERT INTO %s (sender, recipient, time, channel, payload) VALUES (?, ?, ?, ?, ?);", SQL_LOGGER_TABLE_NAME);

	private HikariDataSource dataSource;
	
	public MySQLChatLogger(String address, int port, String database, String username, String password) {
		dataSource = new HikariDataSource();
		dataSource.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?useSSL=false", address, port, database));
		dataSource.setUsername(username);
		dataSource.setPassword(new String(password));
		dataSource.setPoolName("ruinscraft-chat-logger-pool");
		dataSource.setMaximumPoolSize(5);
		dataSource.setConnectionTimeout(3000);
		dataSource.setLeakDetectionThreshold(3000);
		
		try (Connection connection = getConnection()) {
			if (connection.isClosed()) {
				ChatPlugin.warning("Chat logging storage MySQL connection lost");
				return;
			} else {
				ChatPlugin.info("Chat logging storage MySQL connection established");
			}
			
			try (PreparedStatement create = connection.prepareStatement(SQL_CREATE_CHAT_LOG_TABLE)) {
				create.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Callable<Void> log(ChatMessage message) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				try (Connection connection = getConnection()) {
					try (PreparedStatement insert = connection.prepareStatement(SQL_INSERT_LOG)) {
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
				}
				
				return null;
			}
		};
	}

	@Override
	public void close() {
		dataSource.close();
	}

	private Connection getConnection() {
		try {
			return dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
