package com.ruinscraft.chat.api;

import java.util.concurrent.TimeUnit;

// Vanished players do not have active IPlayerStatus\es
public interface IPlayerStatus {

    String getUsername();

    String getGameMode();

    long getUpdatedTime();

    default boolean isOnline() {
        return getUpdatedTime() + TimeUnit.SECONDS.toMillis(3) > System.currentTimeMillis();
    }

}
