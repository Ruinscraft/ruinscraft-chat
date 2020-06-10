package com.ruinscraft.chat.api;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface IOnlinePlayers {

    Set<IChatPlayer> getForNode(UUID nodeId);

    Set<IChatPlayer> getAll();

    Optional<IChatPlayer> find(UUID playerId);

    IChatPlayer get(UUID playerId);

}
