package com.ruinscraft.chat.event;

import com.ruinscraft.chat.player.OnlineChatPlayer;

public class ChatPlayerLoginEvent extends OnlineChatPlayerEvent {

    public ChatPlayerLoginEvent(OnlineChatPlayer onlineChatPlayer) {
        super(onlineChatPlayer);
    }

}
