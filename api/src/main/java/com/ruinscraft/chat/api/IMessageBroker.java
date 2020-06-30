package com.ruinscraft.chat.api;

public interface IMessageBroker {

    /*
     * Outgoing operations
     */
    void pushChatMessage(IChatMessage message, IChatChannel channel);

    void pushOnlinePlayers(IOnlinePlayers onlinePlayers);

    void pushPlayerUpdate(IChatPlayer player);

    /*
     * Incoming operations
     */
    void handleChatMessage(IChatMessage message, IChatChannel channel);

    void handleOnlinePlayers(IOnlinePlayers onlinePlayers);

    void handlePlayerUpdate(IChatPlayer player);

    void close();

}
