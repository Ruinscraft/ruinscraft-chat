package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;
import org.bukkit.Bukkit;

import java.util.Set;

public abstract class BukkitPluginIntegration {

    public BukkitPluginIntegration(String dependency, IChat chat) {
        if (Bukkit.getServer().getPluginManager().getPlugin(dependency) != null) {
            getAdditionalChannels().forEach(channel -> chat.registerChannel(channel));
        }
    }

    public abstract Set<IChatChannel> getAdditionalChannels();

}
