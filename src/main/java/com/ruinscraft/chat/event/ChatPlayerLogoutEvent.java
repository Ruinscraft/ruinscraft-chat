package com.ruinscraft.chat.event;

import com.ruinscraft.chat.ChatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatPlayerLogoutEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private ChatPlayer player;

    public ChatPlayerLogoutEvent(ChatPlayer player) {
        this.player = player;
    }

    public ChatPlayer getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

}
