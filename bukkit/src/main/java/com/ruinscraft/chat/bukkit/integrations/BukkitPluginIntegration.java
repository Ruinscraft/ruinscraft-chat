package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChatChannel;

import java.util.Set;

public abstract class BukkitPluginIntegration {

    public abstract Set<IChatChannel> getAdditionalChannels();
    
}
