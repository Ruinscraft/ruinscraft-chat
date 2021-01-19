package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
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

        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(username);

        if (chatPlayer != null) {
            showInfo(sender, chatPlayer);
        } else {
            chatPlugin.getChatStorage().queryChatPlayer(username).thenAccept(chatPlayerQuery -> {
                if (chatPlayerQuery.hasResults()) {
                    for (ChatPlayer query : chatPlayerQuery.getResults()) {
                        showInfo(sender, query);
                    }
                } else {
                    if (sender != null) {
                        sender.sendMessage(ChatColor.RED + username + " has never played before");
                    }
                }
            });
        }

        return true;
    }

    private void showInfo(CommandSender sender, ChatPlayer chatPlayer) {
        if (chatPlayer instanceof OnlineChatPlayer &&
                !((OnlineChatPlayer) chatPlayer).isVanished()) {
            OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) chatPlayer;
            sender.sendMessage(ChatColor.GOLD + chatPlayer.getMinecraftUsername() + " is" + ChatColor.GREEN + " online!");
            sender.sendMessage(ChatColor.GOLD + "They are currently on: " + ChatColor.LIGHT_PURPLE + onlineChatPlayer.getServerName());
        } else {
            sender.sendMessage(ChatColor.GOLD + chatPlayer.getMinecraftUsername() + " (" + chatPlayer.getMojangId().toString() + ") was last seen ... ago");
        }
    }

}
