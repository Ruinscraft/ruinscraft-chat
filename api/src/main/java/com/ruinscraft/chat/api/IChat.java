package com.ruinscraft.chat.api;

import java.util.Map;

public interface IChat {

    IChatStorage getStorage();

    void registerChannel(IChatChannel chatChannel);

    Map<String, IChatChannel> getRegisteredChannels();

    void start();

    void shutdown();

}
