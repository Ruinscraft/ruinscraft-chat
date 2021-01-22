package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.VaultUtil;
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

            long now = System.currentTimeMillis();
            String group = VaultUtil.getGroup(player);
            String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;
            boolean vanished = false;

            onlineChatPlayer.setUpdatedAt(now);
            onlineChatPlayer.setGroupName(group);
            onlineChatPlayer.setServerName(serverName);
            onlineChatPlayer.setVanished(vanished);

            chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
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
