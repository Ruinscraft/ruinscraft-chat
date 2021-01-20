package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.player.ChatPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private ChatPlayerManager chatPlayerManager;

    public PlayerJoinListener(ChatPlayerManager chatPlayerManager) {
        this.chatPlayerManager = chatPlayerManager;
    }

    @EventHandler
    public void onPlayerPreJoin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        chatPlayerManager.getOrLoad(uuid).join();
    }

}
