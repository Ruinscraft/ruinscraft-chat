package com.ruinscraft.chat.event;

import com.ruinscraft.chat.ChatMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChatMessageEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private ChatMessage message;

    public ChatMessageEvent(ChatMessage message) {
        this.message = message;
    }

    public ChatMessage getMessage() {
        return message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
