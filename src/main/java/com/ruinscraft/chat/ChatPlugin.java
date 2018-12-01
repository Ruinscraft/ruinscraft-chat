package com.ruinscraft.chat;

import org.bukkit.Bukkit;
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
import com.ruinscraft.chat.commands.ChatCommand;
import com.ruinscraft.chat.commands.ClearChatCommand;
import com.ruinscraft.chat.commands.IgnoreCommand;
import com.ruinscraft.chat.commands.NicknameCommand;
import com.ruinscraft.chat.commands.NicknameResetCommand;
import com.ruinscraft.chat.filters.ChatFilterManager;
import com.ruinscraft.chat.listeners.ChatListener;
import com.ruinscraft.chat.listeners.QuitJoinListener;
import com.ruinscraft.chat.logging.ChatLoggingManager;
import com.ruinscraft.chat.messenger.MessageManager;
import com.ruinscraft.chat.messenger.redis.RedisMessageManager;
import com.ruinscraft.chat.players.ChatPlayerManager;

import net.milkbowl.vault.chat.Chat;

public class ChatPlugin extends JavaPlugin implements PluginMessageListener {

	/* ============== Start of statics ============== */
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
		return vaultChat;
	}
	/* ============== End of statics ============== */

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
	private ChatFilterManager chatFilterManager;
	private ChatLoggingManager chatLoggingManager;

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
		ConfigurationSection messagingSection = getConfig().getConfigurationSection("messaging");
		if (messagingSection.getBoolean("redis.use")) {
			messageManager = new RedisMessageManager(messagingSection.getConfigurationSection("redis"));
		}

		/* Setup ChatPlayerManager */
		chatPlayerManager = new ChatPlayerManager(getConfig().getConfigurationSection("player-storage"));

		/* Setup ChatChannelManager */
		chatChannelManager = new ChatChannelManager(getConfig().getConfigurationSection("channels"));

		/* Setup ChatFilterManager */
		chatFilterManager = new ChatFilterManager(getConfig().getConfigurationSection("chat-filters"));

		/* Setup ChatLoggingManager */
		chatLoggingManager = new ChatLoggingManager(getConfig().getConfigurationSection("logging"));

		/* Register PluginMessenger */
		getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);

		/* Setup Vault Chat */
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(Chat.class);
		if (chatProvider != null) {
			vaultChat = chatProvider.getProvider();
		}

		/* In the case of a reload */
		for (Player player : Bukkit.getOnlinePlayers()) {
			getChatPlayerManager().loadChatPlayer(player.getUniqueId());

			if (serverName ==  null) {
				checkServerName();
			}
		}
		
		ChatCommand chatCommand = new ChatCommand(); // listener and command
		
		/* Register Bukkit Listeners */
		pm.registerEvents(new ChatListener(), this);
		pm.registerEvents(new QuitJoinListener(), this);
		pm.registerEvents(chatCommand, this);
		
		
		/* Register Bukkit Commands */
		getCommand("ignore").setExecutor(new IgnoreCommand());
		getCommand("clearchat").setExecutor(new ClearChatCommand());
		getCommand("nickname").setExecutor(new NicknameCommand());
		getCommand("nicknamereset").setExecutor(new NicknameResetCommand());
		getCommand("chat").setExecutor(chatCommand);
	}

	@Override
	public void onDisable() {
		chatChannelManager.unregisterAll();
		
		try {
			messageManager.close();
			chatPlayerManager.close();
			chatLoggingManager.close();
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

	public ChatFilterManager getChatFilterManager() {
		return chatFilterManager;
	}

	public ChatLoggingManager getChatLoggingManager() {
		return chatLoggingManager;
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
