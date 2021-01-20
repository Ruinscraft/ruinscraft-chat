package com.ruinscraft.chat.command.completers;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class FriendsCompleter implements TabCompleter {

    private ChatPlugin chatPlugin;

    public FriendsCompleter(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> friendNames = new ArrayList<>();

        // TODO:

        return friendNames;
    }

}
