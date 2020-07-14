package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.types.*;
import com.ruinscraft.chat.channel.types.pm.PrivateMessageChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashSet;
import java.util.Set;

public class ChatChannelManager {

    private Set<ChatChannel<?>> channels;

    public ChatChannelManager(ConfigurationSection channelSection) {
        /* Setup channels */
        channels = new HashSet<>();

        if (!ChatPlugin.getInstance().getConfig().getBoolean("channels.disable")) {
            if (ChatPlugin.getInstance().getConfig().getBoolean("channels.enable-global")) {
                channels.add(new GlobalChatChannel());
            }

            // add local channel
            switch (channelSection.getString("local.type")) {
                case "default":
                    channels.add(new DefaultLocalChatChannel());
                    break;
                case "plot": // requires PlotSquared
                    channels.add(new PlotLocalChatChannel());
                    break;
                case "hub":
                    channels.add(new HubLocalChatChannel());
                    break;
                case "none":
                    break;
            }
        }

        channels.add(new PrivateMessageChatChannel(channelSection.getConfigurationSection("private-message")));
        channels.add(new MBChatChannel());
        channels.add(new MBHChatChannel());
        channels.add(new MBSChatChannel());
        channels.add(new MBAChatChannel());
        channels.forEach(c -> c.registerCommands());
    }

    public <T extends ChatMessage> ChatChannel<T> getByName(String name) {
        for (ChatChannel<?> chatChannel : channels) {
            if (chatChannel.getName().equalsIgnoreCase(name)) {
                return (ChatChannel<T>) chatChannel;
            }

            if (chatChannel.getPrettyName().equalsIgnoreCase(name)) {
                return (ChatChannel<T>) chatChannel;
            }
        }

        return (ChatChannel<T>) new GlobalChatChannel();
    }

    public ChatChannel<GenericChatMessage> getDefaultChatChannel() {
        if (ChatPlugin.getInstance().getConfig().getBoolean("channels.enable-global")) {
            if (getByName("global") != null) {
                return getByName("global");
            }
        }

        if (getByName("local") != null) {
            return getByName("local");
        }

        return null;
    }

    public Set<ChatChannel<?>> getChatChannels() {
        return channels;
    }

    public Set<ChatChannel<?>> getMuteableChannels() {
        Set<ChatChannel<?>> muteable = new HashSet<>();
        for (ChatChannel<?> channel : channels) {
            if (channel.isMutable()) {
                muteable.add(channel);
            }
        }
        return muteable;
    }

    public Set<ChatChannel<?>> getSpyableChannels() {
        Set<ChatChannel<?>> spyable = new HashSet<>();
        for (ChatChannel<?> channel : channels) {
            if (channel.isSpyable()) {
                spyable.add(channel);
            }
        }
        return spyable;
    }

    public void unregisterAll() {
        channels.forEach(c -> c.unregisterCommands());
        channels.clear();
    }

}
