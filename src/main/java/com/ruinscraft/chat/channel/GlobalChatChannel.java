package com.ruinscraft.chat.channel;

import org.bukkit.ChatColor;

public class GlobalChatChannel extends ChatChannel {

    public GlobalChatChannel() {
        super("default", "global", "", ChatColor.RESET, false);
    }

}
