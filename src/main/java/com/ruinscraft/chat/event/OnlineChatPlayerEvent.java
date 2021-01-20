package com.ruinscraft.chat.event;

import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class OnlineChatPlayerEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private OnlineChatPlayer onlineChatPlayer;

    public OnlineChatPlayerEvent(OnlineChatPlayer onlineChatPlayer) {
        this.onlineChatPlayer = onlineChatPlayer;
    }

    public OnlineChatPlayer getOnlineChatPlayer() {
        return onlineChatPlayer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
