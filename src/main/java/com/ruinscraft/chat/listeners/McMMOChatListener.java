package com.ruinscraft.chat.listeners;

import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.ruinscraft.chat.events.DummyAsyncPlayerChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class McMMOChatListener implements Listener {

    @EventHandler
    public void onMcMMOChatEvent(McMMOChatEvent event) {
        Player player = Bukkit.getPlayer(event.getSender());

        if (player == null) {
            return;
        }

        AsyncPlayerChatEvent dummyEvent = new DummyAsyncPlayerChatEvent(true, player, event.getMessage());

        Bukkit.getServer().getPluginManager().callEvent(event);

        if (dummyEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

}
