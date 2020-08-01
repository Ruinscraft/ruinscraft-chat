package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.core.Chat;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ChatPlayerManager {

    private Chat chat;
    private Map<UUID, ChatPlayer> cache;

    public ChatPlayerManager(Chat chat) {
        this.chat = chat;
        cache = new ConcurrentHashMap<>();
    }

    public boolean isLoaded(UUID mojangId) {
        return cache.containsKey(mojangId);
    }

    public ChatPlayer get(UUID mojangId) {
        return cache.get(mojangId);
    }

    public CompletableFuture<ChatPlayer> getOrLoad(UUID mojangId) {
        if (cache.containsKey(mojangId)) {
            return CompletableFuture.completedFuture(cache.get(mojangId));
        }

        return CompletableFuture.supplyAsync(() -> {
            ChatPlayer chatPlayer = chat.getPlatform().createChatPlayer(mojangId);

            chat.getStorage().loadPlayer(chatPlayer).join();

            cache.put(mojangId, chatPlayer);

            return chatPlayer;
        });
    }

    public void unload(UUID mojangId) {
        cache.remove(mojangId);
    }

}
