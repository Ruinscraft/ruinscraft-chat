package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public NicknameCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        if (label.equalsIgnoreCase("nicknamereset")) {
            onlineChatPlayer.getPersonalizationSettings().setNickname("");

            chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings()).thenRun(() -> {
                onlineChatPlayer.sendMessage(ChatColor.GOLD + "Nickname reset.");
            });

            return true;
        }

        if (args.length < 1) {
            boolean hasNickname = !onlineChatPlayer.getPersonalizationSettings().getNickname().equals("");

            if (hasNickname) {
                player.sendMessage(ChatColor.GOLD + "Your current nickname is: " + onlineChatPlayer.getPersonalizationSettings().getNickname());
                player.sendMessage(ChatColor.GOLD + "Reset it with /nicknamereset");
            } else {
                player.sendMessage(ChatColor.GOLD + "You don't currently have a nickname. You can set one with /" + label + " <name>");
            }
        } else {
            String nickname = args[0];

            if (nickname.length() > 24) {
                player.sendMessage(ChatColor.RED + "Nicknames can only be 24 characters long.");
            } else {
                onlineChatPlayer.getPersonalizationSettings().setNickname(nickname);

                chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings()).thenRun(() -> {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "Your nickname has been changed to: " + nickname);
                });
            }
        }

        return true;
    }

}
