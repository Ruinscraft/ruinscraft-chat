package com.ruinscraft.chat.player;

import com.ruinscraft.chat.channel.ChatChannel;

import java.util.Objects;
import java.util.UUID;

public class ChatPlayer {

    private UUID mojangId;
    private String minecraftUsername;
    private long firstSeen;
    private long lastSeen;
    private ChatChannel focused;

    public ChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused) {
        this.mojangId = mojangId;
        this.minecraftUsername = minecraftUsername;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.focused = focused;
    }

    public UUID getMojangId() {
        return mojangId;
    }

    public String getMinecraftUsername() {
        return minecraftUsername;
    }

    public long getFirstSeen() {
        return firstSeen;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public ChatChannel getFocused() {
        return focused;
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
