package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface IChatStorage {

    CompletableFuture<Void> log(IChatMessageLog chatMessageLog);

    CompletableFuture<Void> loadPlayer(IChatPlayer chatPlayer);

    CompletableFuture<Void> updateStatuses(String gamemode, Set<IChatPlayer> chatPlayers);

    CompletableFuture<Map<String, Set<IPlayerStatus>>> queryStatuses();

}
