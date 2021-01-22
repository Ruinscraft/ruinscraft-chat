package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private ChatPlugin chatPlugin;

    public PlayerJoinQuitListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().getOrLoad(event.getPlayer()).join();
        onlineChatPlayer.setLastSeen(System.currentTimeMillis());
        onlineChatPlayer.setMinecraftUsername(event.getPlayer().getName());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(event.getPlayer());
        chatPlugin.getChatStorage().saveChatPlayer(onlineChatPlayer);
    }

}
