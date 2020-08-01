package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IChatChannel {

    String getName();

    default String getPermission() {
        return null;
    }

    ChatChannelType getType();

    /**
     * Determines if messages should be sent over the Messenger
     * Eg. for Channels that should be "cross-server"
     * @return
     */
    boolean isLocal();

    /**
     * Determines if messages should be filtered
     * Eg. Profanity filter, caps lock filter, etc
     * @return
     */
    boolean isFiltered();

    CompletableFuture<String> getFormatted(IChat chat, IChatMessage chatMessage);

    CompletableFuture<IChatMessageLog> sendMessage(IChat chat, IChatMessage chatMessage);

}
