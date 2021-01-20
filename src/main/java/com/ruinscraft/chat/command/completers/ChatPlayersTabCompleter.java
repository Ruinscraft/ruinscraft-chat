package com.ruinscraft.chat.command.completers;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChatPlayersTabCompleter implements TabCompleter {

    private ChatPlugin chatPlugin;

    public ChatPlayersTabCompleter(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> usernames = new ArrayList<>();

        for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
            usernames.add(onlineChatPlayer.getMinecraftUsername());
        }

        return usernames;
    }

}
