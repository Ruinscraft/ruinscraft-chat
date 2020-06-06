package com.ruinscraft.chat.api;

import java.util.UUID;

public interface IChatMessage {

    long getTime();

    String getSender();

    UUID getSenderId();

    String getContent();

    void applyFormatter(IMessageFormatter formatter);

}
