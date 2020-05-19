package com.ruinscraft.chat.api;

import java.util.Map;

public interface IChat {

    IChatStorage getStorage();

    Map<String, IChatLogger> getLoggers();

    Map<String, IChatChannel> getChannels();

    Map<String, IMessageFilter> getFilters();

    void start() throws Exception;

    void shutdown() throws Exception;

}
