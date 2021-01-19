package com.ruinscraft.chat.player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.storage.ChatPlayerQuery;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatPlayerManager {

    private ChatPlugin chatPlugin;
    private Map<UUID, ChatPlayer> cache;

    public ChatPlayerManager(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        cache = new ConcurrentHashMap<>();
    }

    public CompletableFuture<ChatPlayer> getOrLoad(UUID mojangId) {
        if (cache.containsKey(mojangId)) {
            return CompletableFuture.completedFuture(cache.get(mojangId));
        }

        return CompletableFuture.supplyAsync(() -> {
            ChatPlayerQuery query = chatPlugin.getChatStorage().queryChatPlayer(mojangId).join();
            final ChatPlayer chatPlayer;

            if (query.hasResults()) {
                chatPlayer = query.getFirst();
            } else {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mojangId);
                String username = offlinePlayer.getName();
                long now = System.currentTimeMillis();
                ChatChannel focused = chatPlugin.getChatChannelManager().getDefaultChannel();
                chatPlayer = new ChatPlayer(mojangId, username, now, now, focused);
                chatPlugin.getChatStorage().saveChatPlayer(chatPlayer);
            }

            cache.put(mojangId, chatPlayer);

            return chatPlayer;
        });
    }

    public ChatPlayer get(UUID mojangId) {
        return cache.get(mojangId);
    }

    public ChatPlayer get(Player player) {
        return get(player.getUniqueId());
    }

    public ChatPlayer get(String username) {
        for (ChatPlayer chatPlayer : cache.values()) {
            if (chatPlayer.getMinecraftUsername().equalsIgnoreCase(username)) {
                return chatPlayer;
            }
        }
        return null;
    }

    public void put(UUID mojangId, ChatPlayer chatPlayer) {
        cache.put(mojangId, chatPlayer);
    }

    public void purgeOfflinePlayers() {
        for (OnlineChatPlayer onlineChatPlayer : getOnlineChatPlayers()) {
            long thresholdTime = System.currentTimeMillis()
                    - TimeUnit.SECONDS.toMillis(OnlineChatPlayer.SECONDS_UNTIL_OFFLINE);

            if (onlineChatPlayer.getUpdatedAt() < thresholdTime) {
                cache.remove(onlineChatPlayer.getMojangId());
            }
        }
    }

    public List<OnlineChatPlayer> getOnlineChatPlayers() {
        List<OnlineChatPlayer> onlineChatPlayers = new ArrayList<>();

        for (ChatPlayer chatPlayer : cache.values()) {
            if (chatPlayer instanceof OnlineChatPlayer) {
                onlineChatPlayers.add((OnlineChatPlayer) chatPlayer);
            }
        }

        return onlineChatPlayers;
    }

}
