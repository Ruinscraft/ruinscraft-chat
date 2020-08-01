package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.core.player.ChatPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListener implements Listener {

    private ChatPlayerManager playerManager;

    public JoinQuitListener(ChatPlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        playerManager.getOrLoad(player.getUniqueId());

        VaultUtil.updatePlayerDisplayName(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerManager.unload(player.getUniqueId());
    }

}
