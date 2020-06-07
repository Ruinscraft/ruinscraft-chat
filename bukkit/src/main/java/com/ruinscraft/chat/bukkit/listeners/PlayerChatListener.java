package com.ruinscraft.chat.bukkit.listeners;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.bukkit.events.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.core.message.ChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class PlayerChatListener implements Listener {

    private IChat chat;

    public PlayerChatListener(IChat chat) {
        this.chat = chat;
    }

    @EventHandler
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
        if (event instanceof DummyAsyncPlayerChatEvent) {
            return;
        }

        if (event.isCancelled()) {
            return;
        } else {
            event.setCancelled(true);
        }

        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        String displayname = player.getDisplayName();

        IChatPlayer sender = chat.getChatPlayer(playerId);
        IChatChannel channel = sender.getFocused();
        IChatMessage message = ChatMessage.of(sender, event.getMessage());

        channel.send(message);
    }

}
