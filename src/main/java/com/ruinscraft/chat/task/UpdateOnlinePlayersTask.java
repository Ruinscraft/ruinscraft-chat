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
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);
            long now = System.currentTimeMillis();
            String group = VaultUtil.getGroup(player);
            String serverName = ChatPlugin.serverName == null ? "Unknown" : ChatPlugin.serverName;
            boolean vanished = false;
            OnlineChatPlayer onlineChatPlayer = new OnlineChatPlayer(chatPlayer, now, serverName, group, vanished);

            chatPlugin.getChatStorage().saveOnlineChatPlayer(onlineChatPlayer);
        }

        chatPlugin.getChatStorage().deleteOfflineChatPlayers().thenRun(() -> {
            chatPlugin.getChatStorage().queryOnlineChatPlayers().thenAccept(onlineChatPlayerQuery -> {
                for (OnlineChatPlayer onlineChatPlayer : onlineChatPlayerQuery.getResults()) {
                    chatPlugin.getChatPlayerManager().put(onlineChatPlayer.getMojangId(), onlineChatPlayer);
                }

                chatPlugin.getChatPlayerManager().purgeOfflinePlayers();
            });
        });
    }

}
