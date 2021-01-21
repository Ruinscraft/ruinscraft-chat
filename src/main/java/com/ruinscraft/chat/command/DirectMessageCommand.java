package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.NetworkUtil;
import com.ruinscraft.chat.message.DirectChatChatMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class DirectMessageCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public DirectMessageCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        if (label.toLowerCase().startsWith("r")) {
            // Handle reply
            if (args.length < 1) {
                player.sendMessage(ChatColor.RED + "/" + label + " <msg>");
            } else {
                if (onlineChatPlayer.hasLastDm()) {
                    ChatPlayer recipient = chatPlugin.getChatPlayerManager().get(onlineChatPlayer.getLastDm());

                    if (recipient instanceof OnlineChatPlayer) {
                        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
                        DirectChatChatMessage directChatChatMessage = new DirectChatChatMessage(UUID.randomUUID(), chatPlugin.getServerId(),
                                onlineChatPlayer, recipient, System.currentTimeMillis(), message);

                        chatPlugin.getChatStorage().saveChatMessage(directChatChatMessage).thenRun(() -> {
                            NetworkUtil.sendPrivateChatEventPacket(player, chatPlugin, directChatChatMessage.getId());
                        });
                    } else {
                        player.sendMessage(ChatColor.RED + "No one to reply to.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "No one to reply to.");
                }
            }
        } else {
            // Handle non-reply
            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "/" + label + " <username> <message>");
            } else {
                String target = args[0];
                ChatPlayer recipient = chatPlugin.getChatPlayerManager().get(target);

                if (recipient instanceof OnlineChatPlayer) {
                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    DirectChatChatMessage directChatChatMessage = new DirectChatChatMessage(UUID.randomUUID(), chatPlugin.getServerId(),
                            onlineChatPlayer, recipient, System.currentTimeMillis(), message);

                    chatPlugin.getChatStorage().saveChatMessage(directChatChatMessage).thenRun(() -> {
                        NetworkUtil.sendPrivateChatEventPacket(player, chatPlugin, directChatChatMessage.getId());
                        onlineChatPlayer.setLastDm(recipient.getMojangId());
                    });
                } else {
                    player.sendMessage(ChatColor.RED + target + " is not online.");
                }
            }
        }

        return true;
    }

}
