package com.ruinscraft.chat.player;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.friend.FriendRequest;
import com.ruinscraft.chat.message.MailMessage;

import java.util.*;

public class ChatPlayer {

    private UUID mojangId;
    private String minecraftUsername;
    private long firstSeen;
    private long lastSeen;
    private ChatChannel focused;
    private Set<ChatChannel> mutedChannels;
    private Set<ChatPlayer> mutedPlayers;
    private List<FriendRequest> friends;
    private List<MailMessage> mail;

    public ChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused) {
        this.mojangId = mojangId;
        this.minecraftUsername = minecraftUsername;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.focused = focused;
        mutedChannels = new HashSet<>();
        mutedPlayers = new HashSet<>();
        friends = new ArrayList<>();
        mail = new ArrayList<>();
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

    public List<FriendRequest> getFriends() {
        return friends;
    }

    public boolean setFriends(List<FriendRequest> friends) {
        boolean newFriend = friends.size() > this.friends.size();
        this.friends = friends;
        return newFriend;
    }

    public List<FriendRequest> getAcceptedFriends() {
        List<FriendRequest> accepted = new ArrayList<>();

        for (FriendRequest friendRequest : friends) {
            if (friendRequest.isAccepted()) {
                accepted.add(friendRequest);
            }
        }

        return accepted;
    }

    public List<FriendRequest> getUnacceptedFriends() {
        List<FriendRequest> unaccepted = new ArrayList<>();

        for (FriendRequest friendRequest : friends) {
            if (!friendRequest.isAccepted()) {
                unaccepted.add(friendRequest);
            }
        }

        return unaccepted;
    }

    public boolean isFriend(ChatPlayer chatPlayer) {
        FriendRequest friendRequest = getFriendRequest(chatPlayer);

        if (friendRequest == null) {
            return false;
        }

        return friendRequest.isAccepted();
    }

    public FriendRequest getFriendRequest(ChatPlayer chatPlayer) {
        for (FriendRequest friendRequest : friends) {
            if (friendRequest.getRequester().equals(chatPlayer.mojangId)
                    || friendRequest.getTarget().equals(chatPlayer.mojangId)) {
                return friendRequest;
            }
        }

        return null;
    }

    public List<MailMessage> getMail() {
        return mail;
    }

    public boolean setMail(List<MailMessage> mail) {
        boolean newMail = mail.size() > this.mail.size();
        this.mail = mail;
        return newMail;
    }

    public void markMailRead() {
        for (MailMessage mailMessage : mail) {
            mailMessage.setRead(true);
        }
    }

}
