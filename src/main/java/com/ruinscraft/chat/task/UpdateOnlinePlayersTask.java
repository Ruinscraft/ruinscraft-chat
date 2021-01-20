package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.VaultUtil;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.entity.Player;

public class UpdateOnlinePlayersTask implements Runnable {

    private ChatPlugin chatPlugin;

    public UpdateOnlinePlayersTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        // Find online players who aren't currently marked as online
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);
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
            }

            chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
        }

        chatPlugin.getChatStorage().queryOnlineChatPlayers().thenAccept(onlineChatPlayerQuery -> {
            // Find online players from other servers
            for (OnlineChatPlayer currentlyOnline : onlineChatPlayerQuery.getResults()) {
                chatPlugin.getChatPlayerManager().put(currentlyOnline.getMojangId(), currentlyOnline);
            }
        }).thenRun(() -> {
            // Delete players who have been offline for too long
            chatPlugin.getChatStorage().deleteOfflineChatPlayers().thenRun(() -> {
                chatPlugin.getChatPlayerManager().purgeOfflinePlayers();
            });
        });
    }

}
