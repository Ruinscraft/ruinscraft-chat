package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.UUID;

public interface IChat {

    UUID getNodeId();

    IChatStorage getStorage();

    IOnlinePlayers getOnlinePlayers();

    IChatPlayer getChatPlayer(UUID playerId);

    Map<String, IChatLogger> getLoggers();

    Map<String, IChatChannel> getChannels();

    Map<String, IMessageFilter> getFilters();

    IChatChannel getChannel(String name);

    void start() throws Exception;

    void shutdown() throws Exception;

}
