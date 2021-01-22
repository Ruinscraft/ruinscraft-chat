package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private ChatPlugin chatPlugin;

    public PlayerJoinListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        chatPlugin.getChatPlayerManager().getOrLoad(uuid).join();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        chatPlugin.getUpdateOnlinePlayersThread().updateOnlinePlayer(event.getPlayer());
    }

}
