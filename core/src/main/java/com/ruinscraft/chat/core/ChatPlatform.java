package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.IChat;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public interface ChatPlatform {

    IChat getChat();

    Logger getLogger();

    Set<UUID> getOnlinePlayers();

    boolean playerSendChatMessage(UUID playerId, String message);

    boolean playerHasPermission(UUID playerId, String permission);

    void loadConfigFromDisk(ChatConfig config);

}
