package com.ruinscraft.chat.api;

import java.util.Set;
import java.util.UUID;

public interface IChatPlayer {

    int getId();

    void setId(int id);

    UUID getMojangId();

    String getNickname();

    void setNickname(String nickname);

    Set<IChatPlayer> getBlocked();

    boolean block(IChatPlayer other);

    boolean unblock(IChatPlayer other);

    default boolean isBlocked(IChatPlayer other) {
        return getBlocked().contains(other);
    }

    IChatChannel getFocused();

    void setFocused(IChatChannel channel);

    Set<IChatChannel> getMuted();

    void mute(IChatChannel channel);

    void unmute(IChatChannel channel);

    default boolean isMuted(IChatChannel channel) {
        return getMuted().contains(channel);
    }

    Set<IChatChannel> getSpying();

    void spy(IChatChannel channel);

    void unspy(IChatChannel channel);

    default boolean isSpying(IChatChannel channel) {
        return getSpying().contains(channel);
    }

    String getDisplayName();

    boolean hasPermission(String permission);

    void sendMessage(IChatMessage message, IMessageFormatter formatter);

    void sendMessage(String content);

    void openChatMenu();

    void openChatSpyMenu();

}
