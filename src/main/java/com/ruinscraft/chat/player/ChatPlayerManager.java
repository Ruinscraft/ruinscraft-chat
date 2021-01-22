package com.ruinscraft.chat.player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.GlobalChatChannel;
import com.ruinscraft.chat.storage.query.ChatPlayerQuery;
import com.ruinscraft.chat.storage.query.OnlineChatPlayerQuery;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ChatPlayerManager {

    private ChatPlugin chatPlugin;
    private Map<UUID, ChatPlayer> cache;

    public ChatPlayerManager(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        cache = new ConcurrentHashMap<>();
    }

    public CompletableFuture<OnlineChatPlayer> getOrLoad(Player player) {
        OnlineChatPlayer cached = get(player);

        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return CompletableFuture.supplyAsync(() -> {
            OnlineChatPlayerQuery query = chatPlugin.getChatStorage().queryOnlineChatPlayer(player.getUniqueId()).join();
            final OnlineChatPlayer onlineChatPlayer;

            if (query.hasResults()) {
                onlineChatPlayer = query.getFirst();
            } else {
                ChatPlayer chatPlayer = getOrLoad(player.getUniqueId()).join();
                long updatedAt = System.currentTimeMillis();
                String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;
                String groupName = VaultUtil.getGroup(player);
                boolean vanished = false;
                UUID lastDm = null;
                onlineChatPlayer = new OnlineChatPlayer(chatPlayer, updatedAt, serverName, groupName, vanished, lastDm);

                /* Fetch blocked players */
                chatPlugin.getChatStorage().queryBlocked(onlineChatPlayer).thenAccept(chatPlayerQuery -> {
                    Set<ChatPlayer> blockedChatPlayers = new HashSet<>();
                    for (ChatPlayer blockedChatPlayer : chatPlayerQuery.getResults()) {
                        blockedChatPlayers.add(blockedChatPlayer);
                    }
                    onlineChatPlayer.setBlocked(blockedChatPlayers);
                });
                /* Fetch focused channels */
                chatPlugin.getChatStorage().queryFocusedChannels(onlineChatPlayer).thenAccept(focusedChatChannelNameQuery -> {
                    for (String chatChannelDbName : focusedChatChannelNameQuery.getResults()) {
                        ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(chatChannelDbName);
                        if (!(channel instanceof GlobalChatChannel)) {
                            onlineChatPlayer.setFocused(channel);
                        }
                    }
                });
                /* Fetch personalization settings */
                chatPlugin.getChatStorage().queryPersonalizationSettings(onlineChatPlayer).thenAccept(personalizationSettingsQuery -> {
                    if (personalizationSettingsQuery.hasResults()) {
                        PersonalizationSettings personalizationSettings = personalizationSettingsQuery.getFirst();
                        onlineChatPlayer.setPersonalizationSettings(personalizationSettings);
                    }
                });
                /* Save the new chat player */
                chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
            }

            cache.put(player.getUniqueId(), onlineChatPlayer);

            return onlineChatPlayer;
        });
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
                chatPlayer = new ChatPlayer(mojangId, username, now, now);
                chatPlugin.getChatStorage().saveChatPlayer(chatPlayer);
            }

            cache.put(mojangId, chatPlayer);

            return chatPlayer;
        });
    }

    public ChatPlayer get(UUID mojangId) {
        return cache.get(mojangId);
    }

    public OnlineChatPlayer get(Player player) {
        ChatPlayer cached = get(player.getUniqueId());

        if (cached instanceof OnlineChatPlayer) {
            return (OnlineChatPlayer) cached;
        } else {
            return null;
        }
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

    public List<OnlineChatPlayer> purgeOfflinePlayers() {
        List<OnlineChatPlayer> loggedOut = new ArrayList<>();

        for (OnlineChatPlayer onlineChatPlayer : getOnlineChatPlayers()) {
            if (!onlineChatPlayer.isOnline()) {
                cache.remove(onlineChatPlayer.getMojangId());
                loggedOut.add(onlineChatPlayer);
            }
        }

        return loggedOut;
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
