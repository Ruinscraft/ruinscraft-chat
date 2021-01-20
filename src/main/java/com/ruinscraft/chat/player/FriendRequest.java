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

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public ChatPlayer getOther(ChatPlayer chatPlayer) {
        if (requester.equals(chatPlayer)) {
            return target;
        } else {
            return requester;
        }
    }

    @Override
    public String toString() {
        return "FriendRequest{" +
                "requester=" + requester +
                ", target=" + target +
                ", time=" + time +
                ", accepted=" + accepted +
                '}';
    }

}
