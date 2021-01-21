package com.ruinscraft.chat.channel.staff;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.ChatColor;

public class MBAChatChannel extends MBChatChannel {

    public MBAChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "mba", ChatColor.GRAY + "[" + ChatColor.DARK_RED + "mba" + ChatColor.GRAY + "]", ChatColor.DARK_RED);
    }

}
