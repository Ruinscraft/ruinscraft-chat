package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.core.Chat;

import java.util.Set;

public class TownyIntegration extends BukkitPluginIntegration {

    public TownyIntegration(Chat chat) {
        super("Towny", chat);
    }

    @Override
    public Set<IChatChannel> getChannels() {
        return null;
    }

}
