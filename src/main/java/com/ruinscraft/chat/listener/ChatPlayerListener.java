package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatPlayerListener implements Listener {

    private ChatPlugin chatPlugin;

    public ChatPlayerListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onChatPlayerLogin(ChatPlayerLoginEvent event) {

    }

    @EventHandler
    public void onChatPlayerLogout(ChatPlayerLogoutEvent event) {

    }

}
