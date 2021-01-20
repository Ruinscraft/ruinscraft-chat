package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnblockCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public UnblockCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "/" + label + " <username>");
        } else {
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
            String target = args[0];

            chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
                if (chatPlayerQuery.hasResults()) {
                    ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                    if (onlineChatPlayer.isBlocked(targetChatPlayer)) {
                        onlineChatPlayer.removeBlocked(targetChatPlayer);

                        chatPlugin.getChatStorage().deleteBlock(onlineChatPlayer, targetChatPlayer).thenRun(() -> {
                            onlineChatPlayer.sendMessage(ChatColor.GOLD + targetChatPlayer.getMinecraftUsername() + " has been unblocked.");
                        });
                    } else {
                        onlineChatPlayer.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is not blocked.");
                    }
                } else {
                    onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
                }
            });
        }

        return true;
    }

}
