package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.bukkit.integrations.PlotSquared4Integration;
import com.ruinscraft.chat.bukkit.integrations.TownyIntegration;
import com.ruinscraft.chat.core.Chat;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatPlugin extends JavaPlugin {

    private IChat chat;

    @Override
    public void onEnable() {
        chat = new Chat();

        // start chat
        chat.start();

        // load bukkit plugin integrations
        new PlotSquared4Integration(chat);
        new TownyIntegration(chat);

        // register bukkit chat listener
        getServer().getPluginManager().registerEvents(new PlayerChatListener(), this);

        // register bukkit commands

    }

    @Override
    public void onDisable() {
        chat.shutdown();
    }

}
