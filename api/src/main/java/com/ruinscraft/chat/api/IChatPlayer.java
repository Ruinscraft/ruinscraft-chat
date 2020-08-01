package com.ruinscraft.chat.api;

import java.util.Optional;
import java.util.UUID;

public interface IChatPlayer {

    UUID getMojangId();

    String getUsername();

    String getDisplayName();

    Optional<IPrivateChatChannel> getPrivate();

    void setPrivate(IPrivateChatChannel _private);

    IChatChannel getFocused(IChatChannel fallback);

    void setFocused(IChatChannel focused);

    void setSettings(IChatSettings settings);

    IChatSettings getSettings();

    default boolean isDisabled(IChatChannel channel) {
        return getSettings().getDisabled().contains(channel);
    }

    boolean isIgnoring(IChatPlayer chatPlayer);

    boolean isIgnoring(UUID mojangId);

    boolean hasPermission(String permission);

    void sendMessage(String message);

}
