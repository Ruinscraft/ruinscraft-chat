package com.ruinscraft.chat.bukkit.events;

import com.google.common.collect.Sets;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DummyAsyncPlayerChatEvent extends AsyncPlayerChatEvent {

    public DummyAsyncPlayerChatEvent(boolean async, Player who, String message) {
        super(async, who, message, Sets.newHashSet(who));
    }

}
