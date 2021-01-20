package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.MailMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;

import java.util.List;

public class FetchMailTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchMailTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
            chatPlugin.getChatStorage().queryMailMessages(onlineChatPlayer).thenAccept(mailMessageQuery -> {
                List<MailMessage> newMail = onlineChatPlayer.setMailMessages(mailMessageQuery.getResults());

                boolean shouldAlert = false;

                for (MailMessage mailMessage : newMail) {
                    if (!onlineChatPlayer.isBlocked(mailMessage.getSender())) {
                        shouldAlert = true;
                    }
                }

                if (shouldAlert) {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "You have unread mail! Type /mail to read it.");
                }
            });
        }
    }

}
