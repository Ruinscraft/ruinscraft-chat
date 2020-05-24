package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;

import java.util.Set;

public class TownyIntegration extends BukkitPluginIntegration {

    public TownyIntegration(IChat chat) {
        super("Towny", chat);
    }

    @Override
    public Set<IChatChannel> getChannels() {
        return null;
    }
    
}
