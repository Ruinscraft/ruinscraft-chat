package com.ruinscraft.chat.player;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.MailMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class OnlineChatPlayer extends ChatPlayer {

    public static final int SECONDS_UNTIL_OFFLINE = 5;

    private long updatedAt;
    private String serverName;
    private String groupName;
    private boolean vanished;
    private List<FriendRequest> friendRequests;
    private List<MailMessage> mailMessages;
    private Set<ChatPlayer> blocked;

    public OnlineChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused, long updatedAt, String serverName, String groupName, boolean vanished) {
        super(mojangId, minecraftUsername, firstSeen, lastSeen, focused);
        this.updatedAt = updatedAt;
        this.serverName = serverName;
        this.groupName = groupName;
        this.vanished = vanished;
        friendRequests = new ArrayList<>();
        mailMessages = new ArrayList<>();
        blocked = new HashSet<>();
    }

    public OnlineChatPlayer(ChatPlayer chatPlayer, long updatedAt, String serverName, String groupName, boolean vanished) {
        this(chatPlayer.getMojangId(), chatPlayer.getMinecraftUsername(), chatPlayer.getFirstSeen(), chatPlayer.getLastSeen(), chatPlayer.getFocused(), updatedAt, serverName, groupName, vanished);
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isVanished() {
        return vanished;
    }

    public void setVanished(boolean vanished) {
        this.vanished = vanished;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public boolean setFriendRequests(List<FriendRequest> friendRequests) {
        boolean newFriendRequests = this.friendRequests.size() < friendRequests.size();
        this.friendRequests = friendRequests;
        return newFriendRequests;
    }

    public FriendRequest getFriendRequest(ChatPlayer chatPlayer) {
        for (FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getOther(this).equals(chatPlayer)) {
                return friendRequest;
            }
        }

        return null;
    }

    public boolean isFriend(ChatPlayer chatPlayer) {
        FriendRequest friendRequest = getFriendRequest(chatPlayer);

        if (friendRequest != null && friendRequest.isAccepted()) {
            return true;
        } else {
            return false;
        }
    }

    public List<MailMessage> getMailMessages() {
        return mailMessages;
    }

    public boolean hasMail() {
        return !mailMessages.isEmpty();
    }

    public boolean setMailMessages(List<MailMessage> mailMessages) {
        boolean newMailMessages = this.mailMessages.size() < mailMessages.size();
        this.mailMessages = mailMessages;
        return newMailMessages;
    }

    public Set<ChatPlayer> getBlocked() {
        return blocked;
    }

    public void setBlocked(Set<ChatPlayer> blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked(ChatPlayer chatPlayer) {
        return blocked.contains(chatPlayer);
    }

    public void addBlocked(ChatPlayer chatPlayer) {
        blocked.add(chatPlayer);
    }

    public void removeBlocked(ChatPlayer chatPlayer) {
        blocked.remove(chatPlayer);
    }

    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(getMojangId());

        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        }
    }

}
