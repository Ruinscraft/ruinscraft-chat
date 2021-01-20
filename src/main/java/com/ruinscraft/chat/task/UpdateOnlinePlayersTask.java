package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.VaultUtil;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UpdateOnlinePlayersTask implements Runnable {

    private ChatPlugin chatPlugin;

    public UpdateOnlinePlayersTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        // Find online players who aren't currently marked as online
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player.getUniqueId());
            final OnlineChatPlayer onlineChatPlayer;

            long now = System.currentTimeMillis();
            String group = VaultUtil.getGroup(player);
            String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;
            boolean vanished = false;

            if (chatPlayer instanceof OnlineChatPlayer) {
                onlineChatPlayer = (OnlineChatPlayer) chatPlayer;
                onlineChatPlayer.setUpdatedAt(now);
                onlineChatPlayer.setGroupName(group);
                onlineChatPlayer.setServerName(serverName);
                onlineChatPlayer.setVanished(vanished);
            } else {
                onlineChatPlayer = new OnlineChatPlayer(chatPlayer, now, serverName, group, vanished);

                chatPlugin.getChatPlayerManager().put(player.getUniqueId(), onlineChatPlayer);
                chatPlugin.getChatStorage().queryBlocked(onlineChatPlayer).thenAccept(chatPlayerQuery -> {
                    Set<ChatPlayer> blockedChatPlayers = new HashSet<>();

                    for (ChatPlayer blockedChatPlayer : chatPlayerQuery.getResults()) {
                        blockedChatPlayers.add(blockedChatPlayer);
                    }

                    onlineChatPlayer.setBlocked(blockedChatPlayers);
                });
            }

            chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
        }

        chatPlugin.getChatStorage().queryOnlineChatPlayers().thenAccept(onlineChatPlayerQuery -> {
            List<OnlineChatPlayer> previouslyOnline = chatPlugin.getChatPlayerManager().getOnlineChatPlayers();

            // Find online players from other servers
            for (OnlineChatPlayer currentlyOnline : onlineChatPlayerQuery.getResults()) {
                if (!previouslyOnline.contains(currentlyOnline)) {
                    // Player has joined
                    chatPlugin.getServer().getScheduler().runTask(chatPlugin, () -> {
                        ChatPlayerLoginEvent event = new ChatPlayerLoginEvent(currentlyOnline);
                        chatPlugin.getServer().getPluginManager().callEvent(event);
                    });

                    chatPlugin.getChatPlayerManager().put(currentlyOnline.getMojangId(), currentlyOnline);
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
