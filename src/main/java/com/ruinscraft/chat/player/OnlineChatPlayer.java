package com.ruinscraft.chat.player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.MailMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class OnlineChatPlayer extends ChatPlayer {

    public static final int SECONDS_UNTIL_OFFLINE = 5;

    private long loggedInAt;
    private long updatedAt;
    private String serverName;
    private String groupName;
    private boolean vanished;
    private UUID lastDm;
    private List<FriendRequest> friendRequests;
    private List<MailMessage> mailMessages;
    private Set<ChatPlayer> blocked;
    private ChatChannel focused;
    private PersonalizationSettings personalizationSettings;

    public OnlineChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, long loggedInAt, long updatedAt, String serverName, String groupName, boolean vanished, UUID lastDm) {
        super(mojangId, minecraftUsername, firstSeen, lastSeen);
        this.loggedInAt = loggedInAt;
        this.updatedAt = updatedAt;
        this.serverName = serverName;
        this.groupName = groupName;
        this.vanished = vanished;
        this.lastDm = lastDm;
        friendRequests = new ArrayList<>();
        mailMessages = new ArrayList<>();
        blocked = new HashSet<>();
        personalizationSettings = new PersonalizationSettings(ChatColor.GRAY, "", false, true, false, new ArrayList<>());
    }

    public OnlineChatPlayer(ChatPlayer chatPlayer, long loggedInAt, long updatedAt, String serverName, String groupName, boolean vanished, UUID lastDm) {
        this(chatPlayer.getMojangId(), chatPlayer.getMinecraftUsername(), chatPlayer.getFirstSeen(), chatPlayer.getLastSeen(), loggedInAt, updatedAt, serverName, groupName, vanished, lastDm);
    }

    public long getLoggedInAt() {
        return loggedInAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public boolean isOnline() {
        long check = updatedAt + TimeUnit.SECONDS.toMillis(SECONDS_UNTIL_OFFLINE);
        return check > System.currentTimeMillis();
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

    public UUID getLastDm() {
        return lastDm;
    }

    public boolean hasLastDm() {
        return lastDm != null;
    }

    public void setLastDm(UUID lastDm) {
        this.lastDm = lastDm;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public boolean setFriendRequests(List<FriendRequest> friendRequests) {
        boolean newFriendRequests = this.friendRequests.size() < friendRequests.size();
        this.friendRequests = friendRequests;
        return newFriendRequests;
    }

    public List<FriendRequest> getFriendRequestsInvolving(ChatPlayer chatPlayer) {
        List<FriendRequest> requests = new ArrayList<>();
        for (FriendRequest friendRequest : friendRequests) {
            if (friendRequest.getTarget().equals(chatPlayer)
                    || friendRequest.getRequester().equals(chatPlayer)) {
                requests.add(friendRequest);
            }
        }
        return requests;
    }

    public List<FriendRequest> removeFriendRequestsInvolving(ChatPlayer chatPlayer) {
        List<FriendRequest> requests = getFriendRequestsInvolving(chatPlayer);
        for (FriendRequest request : requests) {
            friendRequests.remove(request);
        }
        return requests;
    }

    public boolean isFriend(ChatPlayer chatPlayer) {
        for (FriendRequest request : getFriendRequestsInvolving(chatPlayer)) {
            if (request.isAccepted()) {
                return true;
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

    public List<MailMessage> setMailMessages(List<MailMessage> mailMessages) {
        List<MailMessage> oldMail = this.mailMessages;
        List<MailMessage> newMail = new ArrayList<>();
        for (MailMessage mailMessage : mailMessages) {
            if (!oldMail.contains(mailMessage)) {
                newMail.add(mailMessage);
            }
        }
        this.mailMessages = mailMessages;
        return newMail;
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

    public ChatChannel getFocused(ChatPlugin chatPlugin) {
        if (focused == null) {
            focused = chatPlugin.getChatChannelManager().getDefaultChannel();
        }
        return focused;
    }

    public void setFocused(ChatChannel focused) {
        this.focused = focused;
    }

    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(getMojangId());
        if (player != null && player.isOnline()) {
            player.sendMessage(message);
        }
    }

    public PersonalizationSettings getPersonalizationSettings() {
        return personalizationSettings;
    }

    public void setPersonalizationSettings(PersonalizationSettings personalizationSettings) {
        this.personalizationSettings = personalizationSettings;
    }

}
