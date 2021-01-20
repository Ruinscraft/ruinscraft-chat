package com.ruinscraft.chat.player;

public class FriendRequest {

    private ChatPlayer requester;
    private ChatPlayer target;
    private long time;
    private boolean accepted;

    public FriendRequest(ChatPlayer requester, ChatPlayer target, long time, boolean accepted) {
        this.requester = requester;
        this.target = target;
        this.time = time;
        this.accepted = accepted;
    }

    public ChatPlayer getRequester() {
        return requester;
    }

    public ChatPlayer getTarget() {
        return target;
    }

    public long getTime() {
        return time;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public ChatPlayer getOther(ChatPlayer chatPlayer) {
        if (requester.getMojangId().equals(chatPlayer.getMojangId())) {
            return target;
        } else {
            return requester;
        }
    }

}
