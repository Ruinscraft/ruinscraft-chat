package com.ruinscraft.chat.logging;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public class ChatLoggingManager implements AutoCloseable {

	private Set<ChatLogger> loggers;
	
	public ChatLoggingManager(ConfigurationSection loggingSection) {
		loggers = new HashSet<>();
		
		if (loggingSection.getBoolean("console.use")) {
			loggers.add(new ConsoleChatLogger());
		}
		
		if (loggingSection.getBoolean("mysql.use")) {
			String address = loggingSection.getString("mysql.address");
			int port = loggingSection.getInt("mysql.port");
			String database = loggingSection.getString("mysql.database");
			String username = loggingSection.getString("mysql.username");
			String password = loggingSection.getString("mysql.password");
			
			loggers.add(new MySQLChatLogger(address, port, database, username, password));
		}
	}
	
	public Set<ChatLogger> getChatLoggers() {
		return loggers;
	}

	@Override
	public void close() throws Exception {
		loggers.forEach(l -> l.close());
		loggers.clear();
	}
	
}
