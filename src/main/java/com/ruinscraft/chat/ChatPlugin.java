package com.ruinscraft.chat;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.listeners.ChatListener;
import com.ruinscraft.chat.listeners.QuitJoinListener;
import com.ruinscraft.chat.messenger.MessageManager;
import com.ruinscraft.chat.messenger.redis.RedisMessageManager;
import com.ruinscraft.chat.players.ChatPlayerManager;

import net.milkbowl.vault.chat.Chat;

public class ChatPlugin extends JavaPlugin implements PluginMessageListener {

	public static final String RUINSCRAFT_CHAT = "ruinscraft-chat";

	private static ChatPlugin instance;
	private static Chat vaultChat;

	public static ChatPlugin getInstance() {
		return instance;
	}

	public static void info(String message) {
		instance.getLogger().info(message);
	}

	public static void warning(String message) {
		instance.getLogger().warning(message);
	}
	
	public static Chat getVaultChat() {
		if (vaultChat == null) {
	        RegisteredServiceProvider<Chat> rsp = instance.getServer().getServicesManager().getRegistration(Chat.class);
	        vaultChat = rsp.getProvider();
		}
		return vaultChat;
	}

	/*
	 * When a message is received by the MessageConsumer,
	 * if it is a ChatMessage, it will have an associated
	 * server where the ChatMessage needs to be received.
	 * The server must be aware of what "server" it actually
	 * is.
	 */
	private String serverName;
	private MessageManager messageManager;
	private ChatPlayerManager chatPlayerManager;
	private ChatChannelManager chatChannelManager;
	
	@Override
	public void onEnable() {
		instance = this;

		PluginManager pm = getServer().getPluginManager();

		saveDefaultConfig();

		/* Check for Vault */
		if (pm.getPlugin("Vault") == null) {
			warning("Vault required");
			pm.disablePlugin(this);
			return;
		}
		
		/* Check for ruinscraft-player-status */
		if (pm.getPlugin("ruinscraft-player-status") == null) {
			warning("ruinscraft-player-status required");
			pm.disablePlugin(this);
			return;
		}

		/* Setup MessageManager*/
		info("Setting up MessageManager");
		ConfigurationSection messagingSection = getConfig().getConfigurationSection("messaging");
		if (messagingSection.getBoolean("redis.use")) {
			messageManager = new RedisMessageManager(messagingSection.getConfigurationSection("redis"));
		}

		info("Setting up ChatPlayerManager");
		/* Setup ChatPlayerManager */
		chatPlayerManager = new ChatPlayerManager(getConfig().getConfigurationSection("player-storage"));

		info("Setting up ChannelManager");
		/* Setup ChatChannelManager */
		chatChannelManager = new ChatChannelManager(getConfig().getConfigurationSection("channels"));


		/* Register Bukkit Listeners */
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new QuitJoinListener(), this);

		/* Register PluginMessenger */
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
	}

	@Override
	public void onDisable() {
		try {
			messageManager.close();
			chatPlayerManager.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		serverName = null;
		instance = null;
	}

	public void checkServerName() {
		if (serverName == null) {
			getServer().getScheduler().runTaskLater(this, () -> {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("GetServer");
				getServer().sendPluginMessage(this, "BungeeCord", out.toByteArray());
			}, 20L);
		}
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServerName() {
		return serverName;
	}

	public MessageManager getMessageManager() {
		return messageManager;
	}

	public ChatPlayerManager getChatPlayerManager() {
		return chatPlayerManager;
	}

	public ChatChannelManager getChatChannelManager() {
		return chatChannelManager;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();

		switch (subchannel) {
		case "GetServer":
			String serverName = in.readUTF();
			setServerName(serverName);
			break;
		}
	}

}
