package com.ruinscraft.chat.bukkit.integrations;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;

import java.util.Set;

public class PlotSquared4Integration extends BukkitPluginIntegration {

    public PlotSquared4Integration(IChat chat) {
        super("PlotSquared", chat);
    }

    @Override
    public Set<IChatChannel> getAdditionalChannels() {
        return null;
    }

}
