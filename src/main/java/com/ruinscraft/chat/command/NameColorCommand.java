package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class NameColorCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public NameColorCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        List<String> chatColors = new ArrayList<>();
        for (ChatColor chatColor : ChatColor.values()) {
            if (chatColor.isColor()) {
                chatColors.add(chatColor.name());
            }
        }

        if (args.length < 1) {
            ChatColor current = onlineChatPlayer.getPersonalizationSettings().getNameColor();
            player.sendMessage(ChatColor.GOLD + "Your current name color is: " + current + current.name());
            player.sendMessage(ChatColor.GOLD + "Valid colors are: " + String.join(", ", chatColors));
            player.sendMessage(ChatColor.GOLD + "/" + label + " <color>");
        } else {
            String colorName = args[0].toUpperCase();

            if (chatColors.contains(colorName)) {
                ChatColor chatColor = ChatColor.valueOf(colorName);

                onlineChatPlayer.getPersonalizationSettings().setNameColor(chatColor);

                chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings()).thenRun(() -> {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "Name color set to: " + chatColor + chatColor.name());
                });
            } else {
                player.sendMessage(ChatColor.GOLD + "Invalid chat color.");
            }
        }

        return true;
    }

}
