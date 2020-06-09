package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.core.Chat;

import java.util.Set;

public class PlotSquared4Integration extends BukkitPluginIntegration {

    public PlotSquared4Integration(Chat chat) {
        super("PlotSquared", chat);
    }

    @Override
    public Set<IChatChannel> getChannels() {
        return null;
    }

}
