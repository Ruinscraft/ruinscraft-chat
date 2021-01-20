package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public BlockCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        if (args.length < 1) {
            List<String> blockedUsernames = new ArrayList<>();

            for (ChatPlayer blockedChatPlayer : onlineChatPlayer.getBlocked()) {
                blockedUsernames.add(blockedChatPlayer.getMinecraftUsername());
            }

            onlineChatPlayer.sendMessage(ChatColor.GOLD + "Currently blocked: " + ChatColor.GRAY + String.join(", ", blockedUsernames));
        } else {
            String target = args[0];

            chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
                if (chatPlayerQuery.hasResults()) {
                    ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                    if (onlineChatPlayer.isBlocked(targetChatPlayer)) {
                        onlineChatPlayer.sendMessage(ChatColor.RED + "You already have " + targetChatPlayer.getMinecraftUsername() + " blocked.");
                    } else {
                        onlineChatPlayer.addBlocked(targetChatPlayer);

                        chatPlugin.getChatStorage().insertBlock(onlineChatPlayer, targetChatPlayer).thenRun(() -> {
                            onlineChatPlayer.sendMessage(ChatColor.GOLD + targetChatPlayer.getMinecraftUsername() + " has been blocked. Use /unblock to unblock.");
                        });
                    }
                } else {
                    onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
                }
            });
        }

        return true;
    }

}
