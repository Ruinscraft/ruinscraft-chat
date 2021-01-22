package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.cinema.TheaterChatChannel;
import com.ruinscraft.chat.channel.plotsquared.PlotChatChannel;
import com.ruinscraft.chat.channel.staff.MBAChatChannel;
import com.ruinscraft.chat.channel.staff.MBChatChannel;
import com.ruinscraft.chat.channel.staff.MBSChatChannel;
import com.ruinscraft.chat.channel.towny.NationChatChannel;
import com.ruinscraft.chat.channel.towny.TownChatChannel;
import com.ruinscraft.chat.channel.towny.TownyGlobalChatChannel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class ChatChannelManager {

    private ChatPlugin chatPlugin;
    private ChatChannel defaultChannel;
    private Set<ChatChannel> channels;

    public ChatChannelManager(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;

        channels = new HashSet<>();

        boolean plotSquared = chatPlugin.getServer().getPluginManager().isPluginEnabled("PlotSquared");
        boolean towny = chatPlugin.getServer().getPluginManager().isPluginEnabled("Towny");
        boolean cinemaDisplays = chatPlugin.getServer().getPluginManager().isPluginEnabled("CinemaDisplays");

        if (plotSquared) {
            channels.add(new PlotChatChannel(chatPlugin));
        }

        if (towny) {
            channels.add(new TownChatChannel(chatPlugin));
            channels.add(new NationChatChannel(chatPlugin));
            defaultChannel = new TownyGlobalChatChannel(chatPlugin);
        }

        if (cinemaDisplays) {
            channels.add(new TheaterChatChannel(chatPlugin));
        }

        if (defaultChannel == null) {
            defaultChannel = new GlobalChatChannel(chatPlugin);
        }

        channels.add(new MBChatChannel(chatPlugin));
        channels.add(new MBSChatChannel(chatPlugin));
        channels.add(new MBAChatChannel(chatPlugin));

        // Global channel will always be available
        channels.add(defaultChannel);

        registerCommands();
    }

    public Set<ChatChannel> getChannels() {
        return channels;
    }

    public ChatChannel getChannel(String pluginName, String name) {
        for (ChatChannel channel : channels) {
            if (channel.getPluginName().equalsIgnoreCase(pluginName)
                    && channel.getName().equalsIgnoreCase(name)) {
                return channel;
            }
        }

        return getDefaultChannel();
    }

    public ChatChannel getChannel(String channelDbName) {
        String pluginName = channelDbName.split(":")[0];
        String channelName = channelDbName.split(":")[1];

        return getChannel(pluginName, channelName);
    }

    public ChatChannel getDefaultChannel() {
        return defaultChannel;
    }

    public void registerCommands() {
        for (ChatChannel channel : channels) {
            Field bukkitCommandMap = null;
            try {
                bukkitCommandMap = chatPlugin.getServer().getClass().getDeclaredField("commandMap");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = null;
            try {
                commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            commandMap.register(channel.getName(), channel.getCommand(chatPlugin));
        }
    }

}
