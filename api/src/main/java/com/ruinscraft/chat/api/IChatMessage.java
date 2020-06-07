package com.ruinscraft.chat.api;

public interface IChatMessage {

    IChatPlayer getSender();

    String getContent();

    long getTime();

}
