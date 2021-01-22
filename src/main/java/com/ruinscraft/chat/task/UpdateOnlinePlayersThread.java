package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.GlobalChatChannel;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.player.PersonalizationSettings;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class UpdateOnlinePlayersThread extends Thread {

    private ChatPlugin chatPlugin;

    public UpdateOnlinePlayersThread(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        while (true) {
            if (chatPlugin == null || !chatPlugin.isEnabled()) {
                return;
            }

            updatePlayers();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOnlinePlayer(Player player) {
        if (!player.isOnline()) {
            return;
        }

        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player.getUniqueId());

        if (chatPlayer == null) {
            chatPlayer = chatPlugin.getChatPlayerManager().getOrLoad(player.getUniqueId()).join();
        }

        final OnlineChatPlayer onlineChatPlayer;

        chatPlayer.setLastSeen(System.currentTimeMillis());
        chatPlayer.setMinecraftUsername(player.getName());

        long now = System.currentTimeMillis();
        String group = VaultUtil.getGroup(player);
        String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;
        boolean vanished = false;
        UUID lastDm = null;

        if (chatPlayer instanceof OnlineChatPlayer) {
            onlineChatPlayer = (OnlineChatPlayer) chatPlayer;
            onlineChatPlayer.setUpdatedAt(now);
            onlineChatPlayer.setGroupName(group);
            onlineChatPlayer.setServerName(serverName);
            onlineChatPlayer.setVanished(vanished);
        } else {
            /* Create new online chat player */
            onlineChatPlayer = new OnlineChatPlayer(chatPlayer, now, serverName, group, vanished, lastDm);

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
        }

        chatPlugin.getChatStorage().saveChatPlayer(chatPlayer);
        chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
    }

    private void updatePlayers() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            updateOnlinePlayer(player);
        }

        // Find online players from the database (who may not be online on THIS server)
        chatPlugin.getChatStorage().queryOnlineChatPlayers().thenAccept(onlineChatPlayerQuery -> {
            for (OnlineChatPlayer onlineChatPlayer : onlineChatPlayerQuery.getResults()) {
                if (!(chatPlugin.getChatPlayerManager().get(onlineChatPlayer.getMojangId()) instanceof OnlineChatPlayer)) {
                    chatPlugin.getChatPlayerManager().put(onlineChatPlayer.getMojangId(), onlineChatPlayer);
                    chatPlugin.getServer().getScheduler().runTask(chatPlugin, () -> {
                        // Player has joined
                        ChatPlayerLoginEvent event = new ChatPlayerLoginEvent(onlineChatPlayer);
                        chatPlugin.getServer().getPluginManager().callEvent(event);
                    });
                }
            }
        }).thenRun(() -> {
            // Delete players who have been offline for too long
            chatPlugin.getChatStorage().deleteOfflineChatPlayers().thenRun(() -> {
                List<OnlineChatPlayer> loggedOut = chatPlugin.getChatPlayerManager().purgeOfflinePlayers();

                for (OnlineChatPlayer onlineChatPlayer : loggedOut) {
                    chatPlugin.getServer().getScheduler().runTask(chatPlugin, () -> {
                        // Player has left
                        ChatPlayerLogoutEvent event = new ChatPlayerLogoutEvent(onlineChatPlayer);
                        chatPlugin.getServer().getPluginManager().callEvent(event);
                    });
                }
            });
        });
    }

}
