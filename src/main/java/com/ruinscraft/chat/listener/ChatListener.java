package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.NetworkUtil;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
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
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
        String message = event.getMessage();
        ChatMessage chatMessage = new ChatMessage(chatPlugin, onlineChatPlayer,
                onlineChatPlayer.getFocused(chatPlugin), message);

        chatPlugin.getChatStorage().saveChatMessage(chatMessage)
                .thenRun(() -> NetworkUtil.sendChatEventPacket(player, chatPlugin, chatMessage.getId()));

        event.setCancelled(true);
    }

}
