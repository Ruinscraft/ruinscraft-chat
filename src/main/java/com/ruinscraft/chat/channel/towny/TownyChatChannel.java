package com.ruinscraft.chat.channel.towny;

import com.ruinscraft.chat.channel.ChatChannel;
import org.bukkit.ChatColor;

public abstract class TownyChatChannel extends ChatChannel {

    public TownyChatChannel(String name, String prefix, ChatColor chatColor, boolean crossServer) {
        super("towny", name, prefix, chatColor, crossServer);
    }

}
