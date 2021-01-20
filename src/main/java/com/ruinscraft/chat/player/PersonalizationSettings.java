package com.ruinscraft.chat.player;

import org.bukkit.ChatColor;

public class PersonalizationSettings {

    private ChatColor nameColor;
    private String nickname;

    public PersonalizationSettings(ChatColor nameColor, String nickname) {
        this.nameColor = nameColor;
        this.nickname = nickname;
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

}
