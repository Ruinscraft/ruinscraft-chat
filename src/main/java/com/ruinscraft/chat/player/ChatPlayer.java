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
    private Set<FriendRequest> friends;
    private List<MailMessage> mail;

    public ChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused) {
        this.mojangId = mojangId;
        this.minecraftUsername = minecraftUsername;
        this.firstSeen = firstSeen;
        this.lastSeen = lastSeen;
        this.focused = focused;
        mutedChannels = new HashSet<>();
        mutedPlayers = new HashSet<>();
        friends = new HashSet<>();
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

    public Set<FriendRequest> getFriends() {
        return friends;
    }

    public Set<FriendRequest> getAcceptedFriends() {
        Set<FriendRequest> accepted = new HashSet<>();

        for (FriendRequest friendRequest : friends) {
            if (friendRequest.isAccepted()) {
                accepted.add(friendRequest);
            }
        }

        return accepted;
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

    public List<MailMessage> getUnreadMail() {
        List<MailMessage> unread = new ArrayList<>();

        for (MailMessage mailMessage : mail) {
            if (!mailMessage.isRead()) {
                unread.add(mailMessage);
            }
        }

        return unread;
    }

    public void markMailRead() {
        for (MailMessage mailMessage : mail) {
            mailMessage.setRead(true);
        }
    }

}
