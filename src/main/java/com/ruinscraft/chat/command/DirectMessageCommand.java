package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.DirectMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.NetworkUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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

                    if (recipient instanceof OnlineChatPlayer && !((OnlineChatPlayer) recipient).isVanished()) {
                        OnlineChatPlayer onlineRecipient = (OnlineChatPlayer) recipient;

                        if (!onlineRecipient.getPersonalizationSettings().isAllowDmsFromAnyone()) {
                            if (!onlineRecipient.isFriend(onlineChatPlayer)) {
                                onlineChatPlayer.sendMessage(ChatColor.RED + onlineRecipient.getMinecraftUsername() + " has only allowed friends to direct message them.");
                                return true;
                            }
                        }

                        String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
                        DirectMessage directMessage = new DirectMessage(onlineChatPlayer, message, chatPlugin.getServerId(), recipient);

                        chatPlugin.getChatStorage().saveChatMessage(directMessage).thenRun(() -> {
                            NetworkUtil.sendPrivateChatEventPacket(chatPlugin, player, directMessage.getId());
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

                if (target.equalsIgnoreCase(player.getName())) {
                    player.sendMessage(ChatColor.RED + "You cannot direct message yourself.");
                    return true;
                }

                ChatPlayer recipient = chatPlugin.getChatPlayerManager().get(target);

                if (recipient instanceof OnlineChatPlayer && !((OnlineChatPlayer) recipient).isVanished()) {
                    OnlineChatPlayer onlineRecipient = (OnlineChatPlayer) recipient;

                    if (!onlineRecipient.getPersonalizationSettings().isAllowDmsFromAnyone()) {
                        if (!onlineRecipient.isFriend(onlineChatPlayer)) {
                            onlineChatPlayer.sendMessage(ChatColor.RED + onlineRecipient.getMinecraftUsername() + " has only allowed friends to direct message them.");
                            return true;
                        }
                    }

                    String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    DirectMessage directMessage = new DirectMessage(onlineChatPlayer, message, chatPlugin.getServerId(), recipient);

                    chatPlugin.getChatStorage().saveChatMessage(directMessage).thenRun(() -> {
                        NetworkUtil.sendPrivateChatEventPacket(chatPlugin, player, directMessage.getId());
                    });
                } else {
                    player.sendMessage(ChatColor.RED + target + " is not online.");
                }
            }
        }

        return true;
    }

}
