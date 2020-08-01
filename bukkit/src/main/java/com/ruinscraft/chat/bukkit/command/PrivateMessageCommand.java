package com.ruinscraft.chat.bukkit.command;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IPrivateChatChannel;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.channel.PrivateChatChannel;
import com.ruinscraft.chat.core.message.ChatMessage;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PrivateMessageCommand implements CommandExecutor {

    private Chat chat;

    public PrivateMessageCommand(Chat chat) {
        this.chat = chat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Specify a username and message.");
            player.sendMessage(ChatColor.RED + "/" + label + " <username> <message>");

            return true;
        }

        else if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Specify a message.");
            player.sendMessage(ChatColor.RED + "/" + label + " <username> <message>");

            return true;
        }

        String target = args[0];

        boolean targetOnline = chat.getPlayerStatusManager().isOnline(target);

        if (!targetOnline) {
            player.sendMessage(ChatColor.RED + target + " is not online.");

            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        ChatPlayer chatPlayer = chat.getPlayerManager().get(player.getUniqueId());
        IPrivateChatChannel channel = new PrivateChatChannel(target);
        IChatMessage chatMessage = new ChatMessage(chatPlayer, message);

        channel.sendMessage(chat, chatMessage);

        chatPlayer.setPrivate(channel);

        return true;
    }

}
