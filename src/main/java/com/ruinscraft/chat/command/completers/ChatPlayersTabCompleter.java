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
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> usernames = new ArrayList<>();

        if (args.length < 1) {
            return usernames;
        }

        String typing = args[args.length - 1];

        for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
            if (!sender.hasPermission("ruinscraft.command.vanish") && onlineChatPlayer.isVanished()) {
                continue;
            }

            if (onlineChatPlayer != null && onlineChatPlayer.getMinecraftUsername() != null) {
                if (onlineChatPlayer.getMinecraftUsername().toLowerCase().startsWith(typing.toLowerCase())) {
                    usernames.add(onlineChatPlayer.getMinecraftUsername());
                }
            }
        }

        return usernames;
    }

}
