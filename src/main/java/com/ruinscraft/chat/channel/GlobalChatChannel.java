package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

public class GlobalChatChannel extends ChatChannel {

    public GlobalChatChannel() {
        super("default", "global", "", ChatColor.RESET, false);
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("g");
        return command;
    }

}
