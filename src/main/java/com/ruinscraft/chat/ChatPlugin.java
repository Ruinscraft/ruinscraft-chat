package com.ruinscraft.chat;

import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.command.*;
import com.ruinscraft.chat.command.completers.BlockedPlayersTabCompleter;
import com.ruinscraft.chat.command.completers.ChatPlayersTabCompleter;
import com.ruinscraft.chat.command.completers.EmptyTabCompleter;
import com.ruinscraft.chat.listener.ChatListener;
import com.ruinscraft.chat.listener.ChatPlayerListener;
import com.ruinscraft.chat.listener.PlayerJoinListener;
import com.ruinscraft.chat.player.ChatPlayerManager;
import com.ruinscraft.chat.storage.ChatStorage;
import com.ruinscraft.chat.storage.impl.MySQLChatStorage;
import com.ruinscraft.chat.task.FetchFriendRequestTask;
import com.ruinscraft.chat.task.FetchMailTask;
import com.ruinscraft.chat.task.FetchServerNameTask;
import com.ruinscraft.chat.task.UpdateOnlinePlayersTask;
import com.ruinscraft.chat.util.NetworkUtil;
import com.ruinscraft.chat.util.SpamHandler;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class ChatPlugin extends JavaPlugin {

    public static String serverName;

    private UUID serverId;
    private ChatStorage chatStorage;
    private ChatPlayerManager chatPlayerManager;
    private ChatChannelManager chatChannelManager;
    private SpamHandler spamHandler;

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

    public SpamHandler getSpamHandler() {
        return spamHandler;
    }

    public List<String> getBadWords() {
        return getConfig().getStringList("badwords");
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
        spamHandler = new SpamHandler(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(chatPlayerManager), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatPlayerListener(this), this);
        getServer().getScheduler().runTaskTimer(this, new UpdateOnlinePlayersTask(this), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new FetchServerNameTask(this), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new FetchFriendRequestTask(this), 20L, 20L);
        getServer().getScheduler().runTaskTimer(this, new FetchMailTask(this), 20L, 20L);

        ListCommand listCommand = new ListCommand(this);
        getCommand("list").setExecutor(listCommand);
        getCommand("listall").setExecutor(listCommand);
        getCommand("seen").setExecutor(new SeenCommand(this));
        getCommand("seen").setTabCompleter(new ChatPlayersTabCompleter(this));
        getCommand("friend").setExecutor(new FriendCommand(this));
        MailCommand mailCommand = new MailCommand(this);
        getCommand("mail").setExecutor(mailCommand);
        getCommand("mail").setTabCompleter(mailCommand);
        getCommand("block").setExecutor(new BlockCommand(this));
        getCommand("block").setTabCompleter(new ChatPlayersTabCompleter(this));
        getCommand("unblock").setExecutor(new UnblockCommand(this));
        getCommand("unblock").setTabCompleter(new BlockedPlayersTabCompleter(this));
        getCommand("nickname").setExecutor(new NicknameCommand(this));
        getCommand("nicknamereset").setExecutor(new NicknameCommand(this));
        getCommand("namecolor").setExecutor(new NameColorCommand(this));
        DirectMessageCommand directMessageCommand = new DirectMessageCommand(this);
        getCommand("directmessage").setExecutor(directMessageCommand);
        getCommand("directmessage").setTabCompleter(new ChatPlayersTabCompleter(this));
        getCommand("reply").setExecutor(directMessageCommand);
        getCommand("reply").setTabCompleter(new EmptyTabCompleter());
        ChatCommand chatCommand = new ChatCommand(this);
        getCommand("chat").setExecutor(chatCommand);
        getCommand("chat").setTabCompleter(chatCommand);

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
