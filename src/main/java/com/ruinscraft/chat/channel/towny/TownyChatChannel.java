package com.ruinscraft.chat.channel.towny;

import com.ruinscraft.chat.channel.ChatChannel;

public abstract class TownyChatChannel extends ChatChannel {

    public TownyChatChannel(String name, String prefix, boolean crossServer) {
        super("towny", name, prefix, crossServer);
    }

}
