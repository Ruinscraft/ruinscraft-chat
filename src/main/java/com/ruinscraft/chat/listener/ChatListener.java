package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatMessageEvent;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.pubsub.PubSub;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private ChatPlugin chatPlugin;

    public ChatListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        PubSub pubSub = chatPlugin.getPubSub();
        ChatMessage chatMessage = ChatMessage.create(chatPlugin, event);

        pubSub.publishChatMessage(chatMessage);
    }

    @EventHandler
    public void onChatMessage(ChatMessageEvent event) {



    }

    @EventHandler
    public void onChatPlayerLogin(ChatPlayerLoginEvent event) {

    }

    @EventHandler
    public void onChatPlayerLogout(ChatPlayerLogoutEvent event) {

    }

}
