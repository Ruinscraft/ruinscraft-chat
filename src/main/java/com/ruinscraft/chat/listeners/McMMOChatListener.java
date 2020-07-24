package com.ruinscraft.chat.listeners;

import com.gmail.nossr50.chat.ChatManager;
import com.gmail.nossr50.chat.ChatManagerFactory;
import com.gmail.nossr50.datatypes.chat.ChatMode;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import com.ruinscraft.chat.events.FullAsyncPlayerChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

public class McMMOChatListener implements Listener {

    @EventHandler
    public void onPartChat(McMMOPartyChatEvent event) {
        Player player = Bukkit.getPlayer(event.getSender());

        if (event == null || event.isCancelled()) {
            return;
        }

        if (!event.isAsynchronous()) {
            event.setCancelled(true);

            Plugin mcmmoPlugin = Bukkit.getServer().getPluginManager().getPlugin("McMMO");

            if (mcmmoPlugin == null || !mcmmoPlugin.isEnabled()) {
                return;
            }

            ChatManager chatManager = ChatManagerFactory.getChatManager(mcmmoPlugin, ChatMode.PARTY);

            Bukkit.getServer().getScheduler().runTaskAsynchronously(mcmmoPlugin, () -> {
                chatManager.handleChat(player, event.getMessage(), true); // Handle chat ASYNC
            });

            return;
        }

        AsyncPlayerChatEvent dummyEvent = new FullAsyncPlayerChatEvent(event.isAsynchronous(), player, event.getMessage());

        Bukkit.getServer().getPluginManager().callEvent(dummyEvent);

        if (dummyEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

}
