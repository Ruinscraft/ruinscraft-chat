package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.command.completers.ChatPlayersTabCompleter;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailCommand implements CommandExecutor, TabCompleter {

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
        player.sendMessage(ChatColor.RED + "/" + label + " <read, clear, send> [username] [message]");
    }

    private void readMail(Player player) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        boolean hadMail = false;

        if (onlineChatPlayer.hasMail()) {
            for (MailMessage mailMessage : onlineChatPlayer.getMailMessages()) {
                if (onlineChatPlayer.isBlocked(mailMessage.getSender())) {
                    continue;
                }

                mailMessage.show(chatPlugin, player);

                hadMail = true;
            }
        }

        if (!hadMail) {
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
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (!chatPlayerQuery.hasResults()) {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never played before.");
            } else {
                ChatPlayer chatPlayerTarget = chatPlayerQuery.getFirst();
                MailMessage mailMessage = new MailMessage(onlineChatPlayer, message, chatPlayerTarget);

                chatPlugin.getChatStorage().saveMailMessage(mailMessage).thenRun(() -> {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "Mail sent!");
                });
            }
        });
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("read");
            options.add("clear");
            options.add("send");
        }

        if (args.length == 2) {
            ChatPlayersTabCompleter chatPlayersTabCompleter = new ChatPlayersTabCompleter(chatPlugin);
            return chatPlayersTabCompleter.onTabComplete(sender, command, label, args);
        }

        return options;
    }

}
