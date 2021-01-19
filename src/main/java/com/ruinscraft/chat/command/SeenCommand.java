package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SeenCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public SeenCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + label + " <username>");
            return true;
        }

        String username = args[0];

        chatPlugin.getChatStorage().queryChatPlayer(username).thenAccept(chatPlayerQuery -> {
            if (!chatPlayerQuery.hasResults()) {
                if (sender != null) {
                    sender.sendMessage(ChatColor.RED + username + " has never played before.");
                }
            } else {
                for (ChatPlayer chatPlayer : chatPlayerQuery.getResults()) {
                    sender.sendMessage(ChatColor.GOLD + chatPlayer.getMinecraftUsername() + " (" + chatPlayer.getMojangId().toString() + ") was last seen ... ago");
                }
            }
        });

        return true;
    }

}
