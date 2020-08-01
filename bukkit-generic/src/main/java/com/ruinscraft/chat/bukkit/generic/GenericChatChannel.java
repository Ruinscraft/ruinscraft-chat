package com.ruinscraft.chat.bukkit.generic;

import com.ruinscraft.chat.api.ChatChannelType;
import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.core.channel.ChatChannel;

import java.util.concurrent.CompletableFuture;

public class GenericChatChannel extends ChatChannel {

    public GenericChatChannel() {
        super("generic", ChatChannelType.NORMAL, true, true);
    }

    @Override
    public CompletableFuture<String> getFormatted(IChat chat, IChatMessage chatMessage) {
        return CompletableFuture.supplyAsync(() -> {
            String format = "%s > %s";

            return String.format(format, chatMessage.getSender().getDisplayName(), chatMessage.getContent());
        });
    }

}
