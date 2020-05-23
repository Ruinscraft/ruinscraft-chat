package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.UUID;

public interface IChat {

    UUID getNodeId();

    IChatStorage getStorage();

    Map<String, IChatLogger> getLoggers();

    Map<String, IChatChannel> getChannels();

    Map<String, IMessageFilter> getFilters();

    // online chat players
    Map<String, IChatPlayer> getPlayers();

    void start() throws Exception;

    void shutdown() throws Exception;

}
