package com.ruinscraft.chat.api;

public interface IChatMessageManager {

    void publish(IChatMessageLog log);

    void consume(IChatMessageLog log);

    void close();

}
