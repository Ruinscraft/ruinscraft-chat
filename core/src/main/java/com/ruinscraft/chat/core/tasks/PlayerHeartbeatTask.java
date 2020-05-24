package com.ruinscraft.chat.core.tasks;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.core.ChatPlatform;

import java.util.Set;
import java.util.UUID;

public class PlayerHeartbeatTask implements Runnable {

    private ChatPlatform platform;

    public PlayerHeartbeatTask(ChatPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void run() {
        IChat chat = platform.getChat();
        Set<UUID> online = platform.getOnlinePlayers();

        for (UUID uuid : online) {
            if (chat.getPlayer(uuid) == null) {
                // we need to load the chat player...
            }
        }

        


    }

}
