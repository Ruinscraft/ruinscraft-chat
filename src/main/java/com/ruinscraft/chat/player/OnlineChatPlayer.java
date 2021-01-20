package com.ruinscraft.chat.player;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.MailMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OnlineChatPlayer extends ChatPlayer {

    public static final int SECONDS_UNTIL_OFFLINE = 5;

    private long updatedAt;
    private String serverName;
    private String groupName;
    private boolean vanished;
    private List<FriendRequest> friendRequests;
    private List<MailMessage> mailMessages;

    public OnlineChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused, long updatedAt, String serverName, String groupName, boolean vanished) {
        super(mojangId, minecraftUsername, firstSeen, lastSeen, focused);
        this.updatedAt = updatedAt;
        this.serverName = serverName;
        this.groupName = groupName;
        this.vanished = vanished;
        friendRequests = new ArrayList<>();
        mailMessages = new ArrayList<>();
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

    public boolean isFriend(ChatPlayer chatPlayer) {
        for (FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getOther(this).getMojangId().equals(chatPlayer.getMojangId())) {
                if (friendRequest.isAccepted()) {
                    return true;
                }
            }
        }

        return false;
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

}
