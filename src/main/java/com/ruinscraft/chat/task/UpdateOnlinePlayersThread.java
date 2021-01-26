package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

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

    private void updatePlayers() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            // Still loading from when they joined
            if (onlineChatPlayer == null) {
                continue;
            }

            if (!onlineChatPlayer.getMinecraftUsername().equals(player.getName())) {
                onlineChatPlayer.setMinecraftUsername(player.getName());
                chatPlugin.getChatStorage().saveChatPlayer(onlineChatPlayer);
            }

            long now = System.currentTimeMillis();
            String group = VaultUtil.getGroup(player);
            String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;

            onlineChatPlayer.setUpdatedAt(now);
            onlineChatPlayer.setGroupName(group);
            onlineChatPlayer.setServerName(serverName);

            chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer).join();
        }

        // Find online players from other servers and add them to the local cache
        chatPlugin.getChatStorage().queryOnlineChatPlayers().thenAccept(onlineChatPlayerQuery -> {
            for (OnlineChatPlayer found : onlineChatPlayerQuery.getResults()) {
                if (chatPlugin.getChatPlayerManager().get(found.getMojangId()) instanceof OnlineChatPlayer) {
                    Player player = Bukkit.getPlayer(found.getMojangId());
                    OnlineChatPlayer cached = (OnlineChatPlayer) chatPlugin.getChatPlayerManager().get(found.getMojangId());

                    if (player == null || !player.isOnline()) {
                        cached.setMinecraftUsername(found.getMinecraftUsername());
                        cached.setUpdatedAt(found.getUpdatedAt());
                        cached.setGroupName(found.getGroupName());
                        cached.setServerName(found.getServerName());
                        cached.setVanished(found.isVanished());
                    }
                } else {
                    chatPlugin.getChatPlayerManager().put(found.getMojangId(), found);
                }
            }
        });

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
    }

}
