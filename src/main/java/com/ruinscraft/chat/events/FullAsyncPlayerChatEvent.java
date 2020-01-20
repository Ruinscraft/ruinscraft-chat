package com.ruinscraft.chat.events;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class FullAsyncPlayerChatEvent extends AsyncPlayerChatEvent {

    public FullAsyncPlayerChatEvent(boolean async, Player who, String message) {
        super(async, who, message, Sets.newHashSet(who));
    }

}
