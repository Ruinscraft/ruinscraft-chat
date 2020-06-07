package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IMessageFormatter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class ChatPlayer implements IChatPlayer {

    private final UUID mojangId;

    private String nickname;
    private IChatChannel focused;
    private Set<IChatPlayer> blocked;
    private Set<IChatChannel> muted;
    private Set<IChatChannel> spying;

    private transient UUID nodeId;
    private transient boolean requiresSave;

    public ChatPlayer(UUID mojangId) {
        this.mojangId = mojangId;
    }

    @Override
    public UUID getMojangId() {
        return mojangId;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public IChatChannel getFocused() {
        if (focused == null || !hasPermission(focused.getPermission())) {
            // set focused to default chat channel?
        }

        return focused;
    }

    @Override
    public void setFocused(IChatChannel channel) {
        if (hasPermission(channel.getPermission())) {
            focused = channel;
        }
    }

    @Override
    public Set<IChatPlayer> getBlocked() {
        if (blocked == null) {
            blocked = new HashSet<>();
        }

        return blocked;
    }

    @Override
    public boolean block(IChatPlayer other) {
        if (getBlocked().add(other)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean unblock(IChatPlayer other) {
        if (getBlocked().remove(other)) {
            return true;
        }

        return false;
    }

    @Override
    public Set<IChatChannel> getMuted() {
        if (muted == null) {
            muted = new HashSet<>();
        }

        return muted.stream()
                .filter(channel -> channel.isMutable())
                .collect(Collectors.toSet());
    }

    @Override
    public void mute(IChatChannel channel) {
        muted.add(channel);
    }

    @Override
    public void unmute(IChatChannel channel) {
        muted.remove(channel);
    }

    @Override
    public Set<IChatChannel> getSpying() {
        if (spying == null) {
            spying = new HashSet<>();
        }

        return spying.stream()
                .filter(channel -> channel.isSpyable() && hasPermission(channel.getPermission()))
                .collect(Collectors.toSet());
    }

    @Override
    public void spy(IChatChannel channel) {
        spying.add(channel);
    }

    @Override
    public void unspy(IChatChannel channel) {
        spying.remove(channel);
    }

    @Override
    public void sendMessage(IChatMessage message, IMessageFormatter formatter) {
        String format = formatter.format(message);
        sendMessage(format);
    }

    public boolean requiresSave() {
        return requiresSave;
    }

}
