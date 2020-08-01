package com.ruinscraft.chat.bukkit.command;

import com.ruinscraft.chat.api.IPrivateChatChannel;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.message.ChatMessage;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand implements CommandExecutor {

    private Chat chat;

    public ReplyCommand(Chat chat) {
        this.chat = chat;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "No message specified.");

            return true;
        }

        ChatPlayer chatPlayer = chat.getPlayerManager().get(player.getUniqueId());

        if (!chatPlayer.getPrivate().isPresent()) {
            player.sendMessage(ChatColor.RED + "No one to reply to.");

            return true;
        }

        IPrivateChatChannel channel = chatPlayer.getPrivate().get();
        boolean targetOnline = chat.getPlayerStatusManager().isOnline(channel.getTo());

        if (!targetOnline) {
            player.sendMessage(ChatColor.RED + channel.getTo() + " is not online.");

            return true;
        }

        String message = String.join(" ", args);
        ChatMessage chatMessage = new ChatMessage(chatPlayer, message);

        channel.sendMessage(chat, chatMessage);

        return true;
    }

}
