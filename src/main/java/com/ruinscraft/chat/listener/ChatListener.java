package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.NetworkUtil;
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
        Player player = event.getPlayer();
        String message = event.getMessage();
        ChatMessage chatMessage = new ChatMessage(chatPlugin, player, message);
        chatPlugin.getChatStorage().saveChatMessage(chatMessage)
                .thenRun(() -> NetworkUtil.sendChatEventPacket(player, chatPlugin, chatMessage.getId()));
        event.setCancelled(true);
    }

}
