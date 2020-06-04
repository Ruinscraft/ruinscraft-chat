package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IOnlinePlayers;

import java.util.*;

public class OnlinePlayers implements IOnlinePlayers {

    private Map<UUID, Set<IChatPlayer>> players;

    @Override
    public Set<IChatPlayer> get(UUID nodeId) {
        return players.get(nodeId);
    }

    @Override
    public Set<IChatPlayer> getAll() {
        // TODO: optimize?
        Set<IChatPlayer> all = new HashSet<>();

        for (UUID nodeId : players.keySet()) {
            all.addAll(players.get(nodeId));
        }

        return all;
    }

    @Override
    public Optional<IChatPlayer> find(UUID playerId) {
        return getAll()
                .stream()
                .filter(cp -> cp.getMojangId().equals(playerId))
                .findFirst();
    }

}
