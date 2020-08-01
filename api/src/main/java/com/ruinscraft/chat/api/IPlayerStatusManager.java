package com.ruinscraft.chat.api;

import java.util.Map;
import java.util.Set;

public interface IPlayerStatusManager {

    Set<IPlayerStatus> getPlayerStatuses(String gamemode);

    Map<String, Set<IPlayerStatus>> getPlayerStatuses();

    boolean isOnline(String username);

    String getGameMode(String username);

}
