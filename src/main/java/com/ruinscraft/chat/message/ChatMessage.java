package com.ruinscraft.chat.message;

import com.ruinscraft.chat.player.ChatPlayer;

import java.util.UUID;

public interface ChatMessage {

    UUID getId();

    UUID getOriginServerId();

    String getChannelDbName();

    long getTime();

    ChatPlayer getSender();

    String getContent();

}
