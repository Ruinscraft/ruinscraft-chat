package com.ruinscraft.chat.player;

import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.channel.ChatChannel;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatPlayer {

    private UUID mojangId;
    private String minecraftUsername;
    private long firstSeen;
    private long lastSeen;
    private ChatChannel focused;
    private Set<ChatChannel> mutedChannels;
    private Set<ChatPlayer> mutedPlayers;
    private Set<ChatMessage> mail;

    public ChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused) {
        this.mojangId = mojangId;
        this.minecraftUsername = minecraftUsername;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.focused = focused;
        mutedChannels = new HashSet<>();
        mutedPlayers = new HashSet<>();
        mail = new HashSet<>();
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

    public Set<ChatChannel> getMutedChannels() {
        return mutedChannels;
    }

    public boolean isMuted(ChatChannel channel) {
        return mutedChannels.contains(channel);
    }

    public Set<ChatPlayer> getMutedPlayers() {
        return mutedPlayers;
    }

    public boolean isMuted(ChatPlayer chatPlayer) {
        return mutedPlayers.contains(chatPlayer);
    }

    public Set<ChatMessage> getMail() {
        return mail;
    }

}
