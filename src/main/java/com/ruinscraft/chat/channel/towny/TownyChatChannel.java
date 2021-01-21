package com.ruinscraft.chat.channel.towny;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import org.bukkit.ChatColor;

public abstract class TownyChatChannel extends ChatChannel {

    public TownyChatChannel(ChatPlugin chatPlugin, String name, String prefix, ChatColor chatColor, boolean crossServer) {
        super(chatPlugin, "towny", name, prefix, chatColor, crossServer);
    }

}
