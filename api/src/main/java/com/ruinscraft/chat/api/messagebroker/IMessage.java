package com.ruinscraft.chat.api.messagebroker;

import com.ruinscraft.chat.api.IChatMessage;

public interface IMessage {

    IChatMessage getContent();

    String serialize();

}
