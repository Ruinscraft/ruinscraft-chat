package com.ruinscraft.chat.api;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IChatStorage {

    CompletableFuture<Void> savePlayer(IChatPlayer player);

    CompletableFuture<Void> loadPlayer(IChatPlayer player);

    CompletableFuture<Void> setOnlinePlayers(Set<IChatPlayer> online);

    CompletableFuture<Void> purgeOfflinePlayers(Set<IChatPlayer> toPurge);

    CompletableFuture<Void> logMessage(IChatMessage message);

    void close();

}
