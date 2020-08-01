package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IChatSettings;
import com.ruinscraft.chat.api.IPrivateChatChannel;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public abstract class ChatPlayer implements IChatPlayer {

    private final UUID mojangId;
    private IPrivateChatChannel _private;
    private IChatChannel focused;
    private IChatSettings settings;
    private Set<IChatPlayer> ignoring;

    public ChatPlayer(UUID mojangId) {
        this.mojangId = mojangId;
        settings = new ChatSettings();
        ignoring = new HashSet<>();
    }

    @Override
    public UUID getMojangId() {
        return mojangId;
    }

    @Override
    public Optional<IPrivateChatChannel> getPrivate() {
        return _private == null ? Optional.empty() : Optional.of(_private);
    }

    @Override
    public void setPrivate(IPrivateChatChannel _private) {
        this._private = _private;
    }

    @Override
    public IChatChannel getFocused(IChatChannel fallback) {
        if (fallback == null) {
            throw new RuntimeException("Fallback channel cannot be null");
        }

        if (focused != null) {
            if (hasPermission(focused.getPermission())) {
                return focused;
            }
        }

        return fallback;
    }

    @Override
    public void setFocused(IChatChannel focused) {
        this.focused = focused;
    }

    @Override
    public void setSettings(IChatSettings settings) {
        this.settings = settings;
    }

    @Override
    public IChatSettings getSettings() {
        return settings;
    }

    @Override
    public boolean isIgnoring(IChatPlayer other) {
        return ignoring.contains(other);
    }

    @Override
    public boolean isIgnoring(UUID mojangId) {
        return ignoring.stream().filter(other -> other.getMojangId().equals(mojangId)).count() > 0;
    }

}
