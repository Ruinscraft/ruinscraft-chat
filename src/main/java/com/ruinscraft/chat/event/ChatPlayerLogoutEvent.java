package com.ruinscraft.chat.event;

import com.ruinscraft.chat.player.OnlineChatPlayer;

public class ChatPlayerLogoutEvent extends OnlineChatPlayerEvent {

    public ChatPlayerLogoutEvent(OnlineChatPlayer onlineChatPlayer) {
        super(onlineChatPlayer);
    }

}
