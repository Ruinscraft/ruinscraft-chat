package com.ruinscraft.chat.player;

import com.ruinscraft.chat.channel.ChatChannel;

import java.util.UUID;

public class OnlineChatPlayer extends ChatPlayer {

    public static final int SECONDS_UNTIL_OFFLINE = 5;

    private long updatedAt;
    private String serverName;
    private String groupName;
    private boolean vanished;

    public OnlineChatPlayer(UUID mojangId, String minecraftUsername, long firstSeen, long lastSeen, ChatChannel focused, long updatedAt, String serverName, String groupName, boolean vanished) {
        super(mojangId, minecraftUsername, firstSeen, lastSeen, focused);
        this.updatedAt = updatedAt;
        this.serverName = serverName;
        this.groupName = groupName;
        this.vanished = vanished;
    }

    public OnlineChatPlayer(ChatPlayer chatPlayer, long updatedAt, String serverName, String groupName, boolean vanished) {
        this(chatPlayer.getMojangId(), chatPlayer.getMinecraftUsername(), chatPlayer.getFirstSeen(), chatPlayer.getLastSeen(), chatPlayer.getFocused(), updatedAt, serverName, groupName, vanished);
        setFriends(chatPlayer.getFriends());
        setMail(chatPlayer.getMail());
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

}
