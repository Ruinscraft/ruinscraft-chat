package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.bukkit.integrations.PlotSquared4Integration;
import com.ruinscraft.chat.bukkit.integrations.TownyIntegration;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.ChatConfig;
import com.ruinscraft.chat.core.ChatPlatform;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ChatPlugin extends JavaPlugin implements ChatPlatform {

    private IChat chat;

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

        // register bukkit chat listener
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // register bukkit commands

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
    public ChatConfig loadConfigFromDisk() {
        saveDefaultConfig();

        ChatConfig config = new ChatConfig();

        // storage
        config.storageType = getConfig().getString("storage.type");
        config.storageMySQLHost = getConfig().getString("storage.mysql.host");
        config.storageMySQLPort = getConfig().getInt("storage.mysql.port");
        config.storageMySQLDatabase = getConfig().getString("storage.mysql.database");
        config.storageMySQLUsername = getConfig().getString("storage.mysql.username");
        config.storageMySQLPassword = getConfig().getString("storage.mysql.password");

        // filters
        config.filtersWebpurifyApiKey = getConfig().getString("filters.webpurify-api-key");

        return config;
    }

    @Override
    public Logger getJLogger() {
        return getLogger();
    }

}
