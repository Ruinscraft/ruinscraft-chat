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
        if (getServer().getPluginManager().getPlugin("PlotSquared") != null) {
            PlotSquared4Integration ps4i = null;
            ps4i.getAdditionalChannels().forEach(channel -> chat.registerChannel(channel));
        }

        if (getServer().getPluginManager().getPlugin("Towny") != null) {
            TownyIntegration ti = null;
            ti.getAdditionalChannels().forEach(channel -> chat.registerChannel(channel));
        }


    }

    @Override
    public void onDisable() {
        chat.shutdown();
    }

}
