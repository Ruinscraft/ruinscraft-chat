package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VanishTask implements Runnable {

    private ChatPlugin chatPlugin;

    public VanishTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (!player.hasPermission("ruinscraft.command.vanish")) {
                onlineChatPlayer.setVanished(false);
            }

            if (onlineChatPlayer.isVanished()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            } else {
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
            }

            for (Player onlinePlayer : chatPlugin.getServer().getOnlinePlayers()) {
                if (onlineChatPlayer.isVanished()) {
                    onlinePlayer.hidePlayer(chatPlugin, player);
                } else {
                    onlinePlayer.showPlayer(chatPlugin, player);
                }
            }
        }
    }

}
