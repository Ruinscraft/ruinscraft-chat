package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessageLog;
import com.ruinscraft.chat.api.IChatMessageManager;
import com.ruinscraft.chat.api.IPrivateChatChannel;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.player.ChatPlayer;

import java.util.Set;

public abstract class ChatMessageManager implements IChatMessageManager {

    private Chat chat;

    public ChatMessageManager(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void consume(IChatMessageLog log) {
        if (log.wasBlocked()) {
            return;
        }

        IChatChannel channel = chat.getChannelManager().get(log.getChannelName());
        Set<ChatPlayer> chatPlayers = chat.getPlatform().getChatPlayers();

        if (channel instanceof IPrivateChatChannel) {
            IPrivateChatChannel privateChatChannel = (IPrivateChatChannel) channel;



        } else {
            for (ChatPlayer chatPlayer : chatPlayers) {
                if (chatPlayer.isIgnoring(log.getSenderId())) {
                    continue;
                }

                if (chatPlayer.isDisabled(channel)) {
                    continue;
                }

                chatPlayer.sendMessage(log.getFormattedMessage());
            }
        }
    }

}
