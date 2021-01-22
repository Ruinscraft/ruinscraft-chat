package com.ruinscraft.chat.util;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.DirectMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class ChatUtil {

    public static void handleChatMessage(ChatPlugin chatPlugin, UUID chatMessageId) {
        chatPlugin.getChatStorage().queryChatMessage(chatMessageId).thenAccept(chatMessageQuery -> {
            if (chatMessageQuery.hasResults()) {
                ChatMessage chatMessage = chatMessageQuery.getFirst();

                chatPlugin.getSpamHandler().addMessage(chatMessage.getSender().getMojangId());

                if (chatMessage instanceof DirectMessage) {
                    DirectMessage directMessage = (DirectMessage) chatMessage;
                    Player senderPlayer = Bukkit.getPlayer(directMessage.getSender().getMojangId());
                    Player recipientPlayer = Bukkit.getPlayer(directMessage.getRecipient().getMojangId());

                    if (senderPlayer != null) {
                        directMessage.show(chatPlugin, senderPlayer);
                    }

                    if (recipientPlayer != null) {
                        directMessage.show(chatPlugin, recipientPlayer);
                    }
                } else {
                    ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(chatMessage.getChannelDbName());

                    if (channel != null) {
                        for (Player player : channel.getRecipients(chatMessage)) {
                            chatMessage.show(chatPlugin, player);
                        }
                    }
                }
            }
        });
    }

}
