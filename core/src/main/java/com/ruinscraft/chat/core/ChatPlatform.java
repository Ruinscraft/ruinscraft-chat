package com.ruinscraft.chat.core;

import com.ruinscraft.chat.core.player.ChatPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public interface ChatPlatform {

    Chat getChat();

    Logger getJLogger();

    Set<UUID> getOnlineIds();

    void loadConfigFromDisk(ChatConfig config);

    void runTaskTimerAsync(Runnable task, long delayTicks, long periodTicks);

    ChatPlayer createChatPlayer(UUID id);

    /**
     * Returns a Mojang UUID for a username
     * Will return null if player has never played before (temporarily)
     *
     * @param username
     * @return the UUID for the username
     */
    UUID getPlayerId(String username);

}
