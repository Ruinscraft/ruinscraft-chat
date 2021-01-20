package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;

public class FetchMailTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchMailTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
            chatPlugin.getChatStorage().queryMailMessages(onlineChatPlayer).thenAccept(mailMessageQuery -> {
                boolean newMailMessages = onlineChatPlayer.setMailMessages(mailMessageQuery.getResults());

                if (newMailMessages) {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "You have unread mail! Type /mail to read it.");
                }
            });
        }
    }

}
