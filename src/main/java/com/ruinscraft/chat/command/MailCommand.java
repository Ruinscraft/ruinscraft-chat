package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);
        List<MailMessage> unread = chatPlayer.getUnreadMail();

        if (unread.isEmpty()) {
            player.sendMessage(ChatColor.GOLD + "You have no unread mail.");
        } else {
            CompletableFuture.runAsync(() -> {
                for (MailMessage mailMessage : unread) {
                    mailMessage.show(chatPlugin, player).join();
                }
            });
        }
    }

    private void clearMail(Player player) {
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);
        chatPlayer.markMailRead();

        CompletableFuture.runAsync(() -> {
            for (MailMessage mailMessage : chatPlayer.getMail()) {
                chatPlugin.getChatStorage().saveMailMessage(mailMessage).join();
            }

            if (player.isOnline()) {
                player.sendMessage(ChatColor.GOLD + "Mail has been cleared.");
            }
        });
    }

    public void sendMail(Player player, String target, String message) {
        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (!chatPlayerQuery.hasResults()) {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.RED + target + " has never played before.");
                }
            } else {
                ChatPlayer chatPlayerTarget = chatPlayerQuery.getFirst();
                MailMessage mailMessage = new MailMessage(UUID.randomUUID(), player.getUniqueId(), chatPlayerTarget.getMojangId(), System.currentTimeMillis(), false, message);
                chatPlugin.getChatStorage().saveMailMessage(mailMessage).thenRun(() -> {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + "Mail sent!");
                    }
                });
            }
        });
    }

}
