package com.ruinscraft.chat.friend;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendRequest {

    private UUID requester;
    private UUID target;
    private long time;
    private boolean accepted;

    public FriendRequest(UUID requester, UUID target, long time, boolean accepted) {
        this.requester = requester;
        this.target = target;
        this.time = time;
        this.accepted = accepted;
    }

    public UUID getRequester() {
        return requester;
    }

    public UUID getTarget() {
        return target;
    }

    public long getTime() {
        return time;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public CompletableFuture<ChatPlayer> getFriend(ChatPlugin chatPlugin, UUID viewer) {
        final UUID friend;

        if (requester.equals(viewer)) {
            friend = target;
        } else {
            friend = requester;
        }

        return chatPlugin.getChatPlayerManager().getOrLoad(friend);
    }

}
