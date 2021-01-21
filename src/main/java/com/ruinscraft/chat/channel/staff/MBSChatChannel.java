package com.ruinscraft.chat.channel.staff;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.ChatColor;

public class MBSChatChannel extends MBChatChannel {

    public MBSChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "mbs", ChatColor.GRAY + "[" + ChatColor.GOLD + "mbs" + ChatColor.GRAY + "]", ChatColor.GOLD);
    }

}
