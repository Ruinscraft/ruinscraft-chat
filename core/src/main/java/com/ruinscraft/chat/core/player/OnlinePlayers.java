package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IOnlinePlayers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OnlinePlayers implements IOnlinePlayers {

    private Map<UUID, Set<IChatPlayer>> players;

    public OnlinePlayers() {
        players = new ConcurrentHashMap<>();
    }

    @Override
    public Set<IChatPlayer> getForNode(UUID nodeId) {
        // ensure there is always at least an empty set returned
        if (!players.containsKey(nodeId)) {
            players.put(nodeId, new HashSet<>());
        }

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

    @Override
    public IChatPlayer get(UUID playerId) {
        return find(playerId).get(); // can return null
    }

}
