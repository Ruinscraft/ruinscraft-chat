package com.ruinscraft.chat.player;

import org.bukkit.ChatColor;

public class PersonalizationSettings {

    private ChatColor nameColor;
    private String nickname;
    private boolean hideProfanity;
    private boolean allowDmsFromAnyone;

    public PersonalizationSettings(ChatColor nameColor, String nickname, boolean hideProfanity, boolean allowDmsFromAnyone) {
        this.nameColor = nameColor;
        this.nickname = nickname;
        this.hideProfanity = hideProfanity;
        this.allowDmsFromAnyone = allowDmsFromAnyone;
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

}
