package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.core.Chat;
import org.bukkit.Bukkit;

import java.util.Set;

public abstract class BukkitPluginIntegration {

    public BukkitPluginIntegration(String dependency, Chat chat) {
        if (Bukkit.getServer().getPluginManager().getPlugin(dependency) != null) {
//            getChannels().forEach(channel -> chat.registerChannel(channel));
        }
    }

    public abstract Set<IChatChannel> getChannels();

}
