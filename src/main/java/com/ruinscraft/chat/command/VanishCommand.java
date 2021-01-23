package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class VanishCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public VanishCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        boolean vanished = !onlineChatPlayer.isVanished();

        if (vanished) {
            player.sendMessage(ChatColor.GOLD + "You are now vanished.");
        } else {
            player.sendMessage(ChatColor.GOLD + "You are no longer vanished.");
            
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }

        onlineChatPlayer.setVanished(vanished);

        return true;
    }

}
