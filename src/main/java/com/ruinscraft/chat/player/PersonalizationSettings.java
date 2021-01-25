package com.ruinscraft.chat.player;

import org.bukkit.ChatColor;

import java.util.List;

public class PersonalizationSettings {

    private ChatColor nameColor;
    private String nickname;
    private boolean hideProfanity;
    private boolean allowDmsFromAnyone;
    private boolean silentJoinLeave;
    private List<String> mutedChannelDbNames;

    public PersonalizationSettings(ChatColor nameColor, String nickname, boolean hideProfanity, boolean allowDmsFromAnyone, boolean silentJoinLeave, List<String> mutedChannelDbNames) {
        this.nameColor = nameColor;
        this.nickname = nickname;
        this.hideProfanity = hideProfanity;
        this.allowDmsFromAnyone = allowDmsFromAnyone;
        this.silentJoinLeave = silentJoinLeave;
        this.mutedChannelDbNames = mutedChannelDbNames;
    }

    public ChatColor getNameColor() {
        return nameColor;
    }

    public void setNameColor(ChatColor nameColor) {
        this.nameColor = nameColor;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isHideProfanity() {
        return hideProfanity;
    }

    public void setHideProfanity(boolean hideProfanity) {
        this.hideProfanity = hideProfanity;
    }

    public boolean isAllowDmsFromAnyone() {
        return allowDmsFromAnyone;
    }

    public void setAllowDmsFromAnyone(boolean allowDmsFromAnyone) {
        this.allowDmsFromAnyone = allowDmsFromAnyone;
    }

    public boolean isSilentJoinLeave() {
        return silentJoinLeave;
    }

    public void setSilentJoinLeave(boolean silentJoinLeave) {
        this.silentJoinLeave = silentJoinLeave;
    }

    public List<String> getMutedChannelDbNames() {
        return mutedChannelDbNames;
    }

    public void setMutedChannelDbNames(List<String> mutedChannelDbNames) {
        this.mutedChannelDbNames = mutedChannelDbNames;
    }

}
