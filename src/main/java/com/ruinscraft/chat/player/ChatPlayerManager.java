package com.ruinscraft.chat.player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.GlobalChatChannel;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatPlayerManager {

    private ChatPlugin chatPlugin;
    private Map<UUID, ChatPlayer> cache;

    public ChatPlayerManager(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        cache = new ConcurrentHashMap<>();
    }

    public OnlineChatPlayer getAndLoad(Player player) {
        ChatPlayer chatPlayer = getAndLoad(player.getUniqueId());

        if (chatPlayer instanceof OnlineChatPlayer) {
            return (OnlineChatPlayer) chatPlayer;
        }

        long updatedAt = System.currentTimeMillis();
        String serverName = ChatPlugin.serverName == null ? "unknown" : ChatPlugin.serverName;
        String groupName = VaultUtil.getGroup(player);
        boolean vanished = false;
        UUID lastDm = null;
        OnlineChatPlayer onlineChatPlayer = new OnlineChatPlayer(chatPlayer, updatedAt, serverName, groupName, vanished, lastDm);

        /* Fetch online chat player */
        chatPlugin.getChatStorage().queryOnlineChatPlayer(player.getUniqueId()).thenAccept(onlineChatPlayerQuery -> {
            if (onlineChatPlayerQuery.hasResults()) {
                OnlineChatPlayer found = onlineChatPlayerQuery.getFirst();
                onlineChatPlayer.setUpdatedAt(found.getUpdatedAt());
                onlineChatPlayer.setServerName(found.getServerName());
                onlineChatPlayer.setGroupName(found.getGroupName());
                onlineChatPlayer.setVanished(found.isVanished());
                onlineChatPlayer.setLastDm(found.getLastDm());
            } else {
                chatPlugin.getServer().getScheduler().runTaskLater(chatPlugin, () -> {
                    chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
                }, 20L);
            }
        });
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

        cache.put(player.getUniqueId(), onlineChatPlayer);

        return onlineChatPlayer;
    }

    public ChatPlayer getAndLoad(UUID mojangId) {
        if (cache.containsKey(mojangId)) {
            return cache.get(mojangId);
        }

        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mojangId);
        ChatPlayer chatPlayer = new ChatPlayer(mojangId, offlinePlayer.getName(), System.currentTimeMillis(), System.currentTimeMillis());

        chatPlugin.getChatStorage().queryChatPlayer(mojangId).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer found = chatPlayerQuery.getFirst();
                chatPlayer.setMinecraftUsername(found.getMinecraftUsername());
                chatPlayer.setFirstSeen(found.getFirstSeen());
                chatPlayer.setLastSeen(found.getLastSeen());
            } else {
                chatPlugin.getChatStorage().saveChatPlayer(chatPlayer);
            }
        });

        cache.put(mojangId, chatPlayer);

        return chatPlayer;
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
