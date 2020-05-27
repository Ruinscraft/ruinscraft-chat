package com.ruinscraft.chat.api.messagebroker;

import java.util.UUID;

public interface IMessage {

    UUID getId();

    long getTime();

    MessageType getType();

    String getPayload();

}
