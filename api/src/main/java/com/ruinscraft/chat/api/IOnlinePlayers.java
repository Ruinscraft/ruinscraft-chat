package com.ruinscraft.chat.api;

import java.util.Set;
import java.util.UUID;

public interface IOnlinePlayers {

    Set<IChatPlayer> getForNode(UUID nodeId);

    Set<IChatPlayer> getAll();

    IChatPlayer get(UUID playerId);

    boolean unload(IChatPlayer player);

    boolean load(IChatPlayer player);

}
