package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.IChat;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public interface ChatPlatform {

    IChat getChat();

    Logger getLogger();

    Set<UUID> getOnlinePlayers();

    void loadConfigFromDisk(ChatConfig config);

}
