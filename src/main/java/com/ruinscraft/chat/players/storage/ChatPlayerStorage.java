package com.ruinscraft.chat.players.storage;

import com.ruinscraft.chat.players.ChatPlayer;

import java.util.concurrent.Callable;

public interface ChatPlayerStorage extends AutoCloseable {

    Callable<Void> loadChatPlayer(ChatPlayer chatPlayer);

    Callable<Void> saveChatPlayer(ChatPlayer chatPlayer);

    @Override
    default void close() {}

}
