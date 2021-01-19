package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.player.ChatPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private ChatPlayerManager chatPlayerManager;

    public PlayerJoinListener(ChatPlayerManager chatPlayerManager) {
        this.chatPlayerManager = chatPlayerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        chatPlayerManager.getOrLoad(player.getUniqueId());
    }

}
