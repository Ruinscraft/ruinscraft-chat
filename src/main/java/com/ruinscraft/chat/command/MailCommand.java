package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

public class MailCommand implements CommandExecutor {

    // TODO: add long cooldown

    private ChatPlugin chatPlugin;

    public MailCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            showHelp(player, label);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "read":
                readMail(player);
                return true;
            case "clear":
                clearMail(player);
                return true;
            case "send":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "/" + label + " send <username> <message>");
                    return true;
                }
                String target = args[1];
                String message = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                sendMail(player, target, message);
                return true;
            default:
                showHelp(player, label);
                return true;
        }
    }

    private void showHelp(Player player, String label) {
        player.sendMessage(ChatColor.RED + "/" + label + " <read/clear/send> [username] [message]");
    }

    private void readMail(Player player) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        if (onlineChatPlayer.hasMail()) {
            for (MailMessage mailMessage : onlineChatPlayer.getMailMessages()) {
                mailMessage.show(player);
            }
        } else {
            player.sendMessage(ChatColor.GOLD + "You have no unread mail.");
        }
    }

    private void clearMail(Player player) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        for (MailMessage mailMessage : onlineChatPlayer.getMailMessages()) {
            mailMessage.setRead(true);
            chatPlugin.getChatStorage().saveMailMessage(mailMessage);
        }

        player.sendMessage(ChatColor.GOLD + "Cleared mail.");
    }

    public void sendMail(Player player, String target, String message) {
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (!chatPlayerQuery.hasResults()) {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.RED + target + " has never played before.");
                }
            } else {
                ChatPlayer chatPlayerTarget = chatPlayerQuery.getFirst();
                MailMessage mailMessage = new MailMessage(UUID.randomUUID(), chatPlayer, chatPlayerTarget, System.currentTimeMillis(), false, message);

                chatPlugin.getChatStorage().saveMailMessage(mailMessage).thenRun(() -> {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + "Mail sent!");
                    }
                });
            }
        });
    }

}
