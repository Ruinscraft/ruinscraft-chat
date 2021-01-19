package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.cinema.TheaterChatChannel;
import com.ruinscraft.chat.channel.plotsquared.PlotChatChannel;
import com.ruinscraft.chat.channel.towny.AllianceChatChannel;
import com.ruinscraft.chat.channel.towny.NationChatChannel;
import com.ruinscraft.chat.channel.towny.TownChatChannel;

import java.util.HashMap;
import java.util.Map;

public class ChatChannelManager {

    private Map<String, ChatChannel> channels;

    public ChatChannelManager(ChatPlugin chatPlugin) {
        channels = new HashMap<>();

        boolean plotSquared = chatPlugin.getServer().getPluginManager().isPluginEnabled("PlotSquared");
        boolean towny = chatPlugin.getServer().getPluginManager().isPluginEnabled("Towny");
        boolean cinemaDisplays = chatPlugin.getServer().getPluginManager().isPluginEnabled("CinemaDisplays");

        if (plotSquared) {
            channels.put("plot", new PlotChatChannel());
        }

        if (towny) {
            channels.put("town", new TownChatChannel());
            channels.put("nation", new NationChatChannel());
            channels.put("alliance", new AllianceChatChannel());
        }

        if (cinemaDisplays) {
            channels.put("theater", new TheaterChatChannel());
        }

        // Global channel will always be available
        channels.put("global", new GlobalChatChannel());
    }

    public ChatChannel getChannel(String name) {
        return channels.get(name);
    }

    public ChatChannel getDefaultChannel() {
        return channels.get("global");
    }

    public boolean hasChannel(String name) {
        return channels.containsKey(name);
    }

}
