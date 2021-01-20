package com.ruinscraft.chat.player;

import java.util.Objects;
import java.util.UUID;

public class ChatPlayer {

    private UUID mojangId;
    private String minecraftUsername;
    private long firstSeen;
    private long lastSeen;

    public ChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen) {
        this.mojangId = mojangId;
        this.minecraftUsername = minecraftUsername;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
    }

    public UUID getMojangId() {
        return mojangId;
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public void setMinecraftUsername(String minecraftUsername) {
        this.minecraftUsername = minecraftUsername;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    @Override
    public boolean equals(Object o) {
        ChatPlayer that = (ChatPlayer) o;
        return Objects.equals(mojangId, that.mojangId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mojangId);
    }

}
