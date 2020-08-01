package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.ChatChannelType;
import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IPrivateChatChannel;

import java.util.concurrent.CompletableFuture;

public class PrivateChatChannel extends ChatChannel implements IPrivateChatChannel {

    private String to;

    public PrivateChatChannel(String to) {
        super("private", ChatChannelType.PRIVATE, false, true);
    }

    @Override
    public String getTo() {
        return to;
    }

    @Override
    public CompletableFuture<String> getFormatted(IChat chat, IChatMessage chatMessage) {
        return null;
    }

}
