package com.ruinscraft.chat.listeners;

import com.ruinscraft.chat.ChatPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitJoinListener implements Listener {

    private static ChatPlugin chatPlugin = ChatPlugin.getInstance();

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        chatPlugin.getChatPlayerManager().unloadChatPlayer(player.getUniqueId());
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        chatPlugin.getChatPlayerManager().loadChatPlayer(player.getUniqueId());

        chatPlugin.checkServerName();
    }

}
