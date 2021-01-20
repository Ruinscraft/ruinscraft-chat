package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FetchMailTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchMailTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            chatPlugin.getChatStorage().queryMailMessages(onlineChatPlayer).thenAccept(mailMessageQuery -> {
                boolean newMailMessages = onlineChatPlayer.setMailMessages(mailMessageQuery.getResults());

                if (newMailMessages) {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + "You have unread mail! Type /mail to read it.");
                    }
                }
            });
        }
    }

}
