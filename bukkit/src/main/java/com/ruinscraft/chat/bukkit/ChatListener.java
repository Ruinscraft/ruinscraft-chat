package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.channel.ChatChannel;
import com.ruinscraft.chat.core.message.ChatMessage;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private Chat chat;

    public ChatListener(Chat chat) {
        this.chat = chat;
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (event instanceof DummyAsyncPlayerChatEvent) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        String message = event.getMessage();

        VaultUtil.updatePlayerDisplayName(player);

        if (!chat.getPlayerManager().isLoaded(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Could not process message.");
            event.setCancelled(true);
            return;
        }

        ChatPlayer chatPlayer = chat.getPlayerManager().get(player.getUniqueId());
        ChatMessage chatMessage = new ChatMessage(chatPlayer, message);
        ChatChannel fallback = chat.getChannelManager().getDefault();

        chatPlayer.getFocused(fallback).sendMessage(chat, chatMessage).thenAcceptAsync(log -> {
            if (log.wasBlocked()) {
                player.sendMessage(ChatColor.RED + "Could not process message.");
            }
        });
    }

}
