package com.ruinscraft.chat.api;

import java.util.Set;
import java.util.UUID;

public interface IChatPlayer {

    UUID getMojangId();

    String getNickname();

    void setNickname(String nickname);

    Set<String> getIgnoring();

    // can be username or uuid
    void ignore(String user);

    // can be username or uuid
    void unignore(String user);

    default boolean isIgnoring(String user) {
        return getIgnoring().contains(user);
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

    boolean hasPermission(String permission);

    void sendMessage(String message);

}
