package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.UUID;

public interface IChat {

    UUID getNodeId(); // TODO: would this be more implementation specific, ie Core?

    IChatStorage getStorage();

    IMessageBroker getMessageBroker();

    IOnlinePlayers getOnlinePlayers();

    IChatPlayer getChatPlayer(UUID playerId);

    Map<String, IChatLogger> getLoggers();

    Map<String, IChatChannel> getChannels();

    Map<String, IMessageFilter> getFilters();

    IChatChannel getChannel(String name);

    IChatChannel getDefaultChannel();

    void start() throws Exception;

    void shutdown() throws Exception;

}
