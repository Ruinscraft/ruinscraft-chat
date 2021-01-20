package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class FetchMailTask implements Runnable {

    private ChatPlugin chatPlugin;
    private Map<Player, Long> lastNotified;

    public FetchMailTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        lastNotified = new HashMap<>();
    }

    @Override
    public void run() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

            chatPlugin.getChatStorage().queryMailMessages(player.getUniqueId()).thenAccept(mailMessageQuery -> {
                boolean newMail = chatPlayer.setMail(mailMessageQuery.getResults());

                if (newMail) {
                    player.sendMessage(ChatColor.GOLD + "You have unread mail! Type /mail to read it.");
                }
            });
        }
    }

}
