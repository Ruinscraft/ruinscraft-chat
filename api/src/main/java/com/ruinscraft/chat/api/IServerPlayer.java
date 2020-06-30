package com.ruinscraft.chat.api;

public interface IServerPlayer {

    /**
     * Should return something like:
     * [Prefix] Username
     *
     * Typically, this would be automatically created by the platform such as org.bukkit.entity.Player#getDisplayName()
     * @return
     */
    String getDisplayName();

    boolean hasPermission(String permission);

    void sendMessage(IChatMessage message, IMessageFormatter formatter);

    void sendMessage(String content);

    void openChatMenu();

    void openChatSpyMenu();

}
