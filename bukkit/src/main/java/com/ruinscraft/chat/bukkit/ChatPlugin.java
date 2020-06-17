package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.bukkit.commands.ListCommandExecutor;
import com.ruinscraft.chat.bukkit.integrations.PlotSquared4Integration;
import com.ruinscraft.chat.bukkit.integrations.TownyIntegration;
import com.ruinscraft.chat.bukkit.listeners.PlayerChatListener;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.ChatConfig;
import com.ruinscraft.chat.core.ChatPlatform;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChatPlugin extends JavaPlugin implements ChatPlatform {

    private Chat chat;

    @Override
    public void onEnable() {
        chat = new Chat(this);

        // start chat
        try {
            chat.start();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // load bukkit plugin integrations
        new PlotSquared4Integration(chat);
        new TownyIntegration(chat);

        // register bukkit commands
        getCommand("list").setExecutor(new ListCommandExecutor(chat));

        // register bukkit listeners
        getServer().getPluginManager().registerEvents(new PlayerChatListener(chat), this);
    }

    @Override
    public void onDisable() {
        try {
            chat.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Chat getChat() {
        return chat;
    }

    @Override
    public void loadConfigFromDisk(ChatConfig config) {
        saveDefaultConfig();

        // storage
        config.storageType = getConfig().getString("storage.type");
        config.storageMySQLHost = getConfig().getString("storage.mysql.host");
        config.storageMySQLPort = getConfig().getInt("storage.mysql.port");
        config.storageMySQLDatabase = getConfig().getString("storage.mysql.database");
        config.storageMySQLUsername = getConfig().getString("storage.mysql.username");
        config.storageMySQLPassword = getConfig().getString("storage.mysql.password");

        // filters
        config.filtersWebpurifyApiKey = getConfig().getString("filters.webpurify-api-key");
    }

    @Override
    public void runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks) {
        getServer().getScheduler().runTaskTimerAsynchronously(this, task, delayTicks, periodTicks);
    }

    @Override
    public ChatPlayer createChatPlayer(UUID id) {
        return new BukkitChatPlayer(id, chat.getNodeId(), chat);
    }

    @Override
    public UUID getPlayerId(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);

        if (offlinePlayer.hasPlayedBefore()) {
            return offlinePlayer.getUniqueId();
        } else {
            return null;
        }
    }

    @Override
    public Logger getJLogger() {
        return getLogger();
    }

    @Override
    public Set<UUID> getOnlineIds() {
        return getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());
    }

}
