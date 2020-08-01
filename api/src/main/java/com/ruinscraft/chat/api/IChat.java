package com.ruinscraft.chat.api;

public interface IChat {

    ChatConfig getConfig();

    IChatStorage getStorage();

    IChatMessageManager getMessageManager();

    IPlayerStatusManager getPlayerStatusManager();

    void run(Runnable runnable);

    void runAsync(Runnable runnable);

}
