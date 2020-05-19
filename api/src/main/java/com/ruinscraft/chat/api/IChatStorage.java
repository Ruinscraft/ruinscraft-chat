package com.ruinscraft.chat.api;

public interface IChatStorage {

    void savePlayer(IChatPlayer player);

    void loadPlayer(IChatPlayer player);

    void logMessage(IChatMessage message);

    void close();

}
