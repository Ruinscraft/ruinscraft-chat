package com.ruinscraft.chat.core.tasks;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.core.ChatPlatform;

public class PlayerHeartbeatTask implements Runnable {

    private ChatPlatform platform;

    public PlayerHeartbeatTask(ChatPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void run() {
        IChat chat = platform.getChat();



    }

}
