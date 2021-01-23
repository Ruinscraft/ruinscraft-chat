package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.NetworkUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private ChatPlugin chatPlugin;

    public ChatListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event instanceof DummyAsyncPlayerChatEvent) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
        String message = event.getMessage();

        DummyAsyncPlayerChatEvent dummyEvent = new DummyAsyncPlayerChatEvent(event.isAsynchronous(), player, message);
        chatPlugin.getServer().getPluginManager().callEvent(dummyEvent);

        if (dummyEvent.isCancelled()) {
            return;
        }

        ChatMessage chatMessage = new ChatMessage(onlineChatPlayer, message, chatPlugin.getServerId(),
                onlineChatPlayer.getFocused(chatPlugin).getDatabaseName());

        chatPlugin.getChatStorage().saveChatMessage(chatMessage)
                .thenRun(() -> NetworkUtil.sendChatEventPacket(chatPlugin, player, chatMessage.getId()));
    }

}
