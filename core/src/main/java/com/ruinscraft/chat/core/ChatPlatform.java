package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.ChatConfig;
import com.ruinscraft.chat.core.player.ChatPlayer;

import java.util.Set;
import java.util.UUID;

public interface ChatPlatform {

    ChatConfig loadConfig();

    void run(Runnable runnable);

    void runAsync(Runnable runnable);

    void runAsyncRepeat(Runnable runnable, long delayTicks, long periodTicks);

    void log(String message);

    void warn(String message);

    Set<ChatPlayer> getChatPlayers();

    ChatPlayer createChatPlayer(UUID mojangId);

}
