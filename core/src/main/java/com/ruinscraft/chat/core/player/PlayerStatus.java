package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IPlayerStatus;

public class PlayerStatus implements IPlayerStatus {

    private final String username;
    private final String gamemode;
    private final long updatedAt;

    public PlayerStatus(String username, String gamemode, long updatedAt) {
        this.username = username;
        this.gamemode = gamemode;
        this.updatedAt = updatedAt;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getGameMode() {
        return gamemode;
    }

    @Override
    public long getUpdatedTime() {
        return updatedAt;
    }

}
