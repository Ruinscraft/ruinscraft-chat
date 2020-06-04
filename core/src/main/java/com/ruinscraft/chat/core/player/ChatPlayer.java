package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class ChatPlayer implements IChatPlayer {

    private final UUID mojangId;

    private String nickname;
    private Set<String> ignoring;
    private IChatChannel focused;
    private Set<IChatChannel> muted;
    private Set<IChatChannel> spying;

    private transient UUID nodeId;

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
    public Set<String> getIgnoring() {
        return ignoring;
    }

    @Override
    public void ignore(String user) {
        ignoring.add(user);
    }

    @Override
    public void unignore(String user) {
        ignoring.remove(user);
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
    public Set<IChatChannel> getMuted() {
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
    public void sendMessage(IChatMessage message) {

    }

}
