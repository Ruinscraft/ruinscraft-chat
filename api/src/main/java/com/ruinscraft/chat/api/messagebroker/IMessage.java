package com.ruinscraft.chat.api.messagebroker;

public interface IMessage {

    MessageType getType();

    String getPayload();

}
