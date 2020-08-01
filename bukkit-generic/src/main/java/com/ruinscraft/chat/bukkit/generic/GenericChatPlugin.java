package com.ruinscraft.chat.bukkit.generic;

import com.ruinscraft.chat.bukkit.ChatPlugin;
import com.ruinscraft.chat.core.channel.ChatChannel;

import java.util.HashSet;
import java.util.Set;

public class GenericChatPlugin extends ChatPlugin {

    private ChatChannel defaultChannel;
    private Set<ChatChannel> channels;

    @Override
    public void onEnable() {
        super.onEnable();

        getLogger().info("Using Generic implementation");
    }

    @Override
    protected ChatChannel getDefaultChannel() {
        if (defaultChannel == null) {
            defaultChannel = new GenericChatChannel();
        }

        return defaultChannel;
    }

    @Override
    protected Set<ChatChannel> getChannels() {
        if (channels == null) {
            channels = new HashSet<>();

            channels.add(getDefaultChannel());
        }

        return channels;
    }

}
