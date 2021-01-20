package com.ruinscraft.chat;

import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.command.FriendCommand;
import com.ruinscraft.chat.command.MailCommand;
import com.ruinscraft.chat.command.completers.ChatPlayersTabCompleter;
import com.ruinscraft.chat.command.ListCommand;
import com.ruinscraft.chat.command.SeenCommand;
import com.ruinscraft.chat.command.completers.FriendsCompleter;
import com.ruinscraft.chat.listener.ChatListener;
import com.ruinscraft.chat.listener.PlayerJoinListener;
import com.ruinscraft.chat.player.ChatPlayerManager;
import com.ruinscraft.chat.storage.ChatStorage;
import com.ruinscraft.chat.storage.impl.MySQLChatStorage;
import com.ruinscraft.chat.task.FetchMailTask;
import com.ruinscraft.chat.task.FetchServerNameTask;
import com.ruinscraft.chat.task.UpdateOnlinePlayersTask;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class ChatPlugin extends JavaPlugin {

    public static String serverName;

    private UUID serverId;
    private ChatStorage chatStorage;
    private ChatPlayerManager chatPlayerManager;
    private ChatChannelManager chatChannelManager;

    public UUID getServerId() {
        return serverId;
    }

    public ChatStorage getChatStorage() {
        return chatStorage;
    }

    public ChatPlayerManager getChatPlayerManager() {
        return chatPlayerManager;
    }

    public ChatChannelManager getChatChannelManager() {
        return chatChannelManager;
    }

    @Override
    public void onEnable() {
        serverId = UUID.randomUUID();

        saveDefaultConfig();

        String mysqlHost = getConfig().getString("storage.mysql.host");
        int mysqlPort = getConfig().getInt("storage.mysql.port");
        String mysqlDatabase = getConfig().getString("storage.mysql.database");
        String mysqlUsername = getConfig().getString("storage.mysql.username");
        String mysqlPassword = getConfig().getString("storage.mysql.password");

        chatStorage = new MySQLChatStorage(this, mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword);
        chatPlayerManager = new ChatPlayerManager(this);
        chatChannelManager = new ChatChannelManager(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(chatPlayerManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getScheduler().runTaskTimer(this, new UpdateOnlinePlayersTask(this), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new FetchServerNameTask(this), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new FetchMailTask(this), 20L, 20L);

        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("seen").setExecutor(new SeenCommand(this));
        getCommand("seen").setTabCompleter(new ChatPlayersTabCompleter(this));
        getCommand("friend").setExecutor(new FriendCommand(this));
        getCommand("mail").setExecutor(new MailCommand(this));
        getCommand("mail").setTabCompleter(new FriendsCompleter(this));

        VaultUtil.init();
        NetworkUtil.register(this);

        for (Player player : getServer().getOnlinePlayers()) {
            chatPlayerManager.getOrLoad(player.getUniqueId());
        }
    }

    @Override
    public void onDisable() {
        NetworkUtil.unregister(this);
    }

}
