package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IOnlinePlayers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OnlinePlayers implements IOnlinePlayers {

    // maps Mojang UUID to IChatPlayer (for quicker lookups)
    private Map<UUID, ChatPlayer> players;

    public OnlinePlayers() {
        players = new ConcurrentHashMap<>();
    }

    @Override
    public Set<IChatPlayer> getForNode(UUID nodeId) {
        Set<IChatPlayer> forNode = new HashSet<>();

        players.values().stream().forEach(player -> {
            if (player.getNodeId().equals(nodeId)) {
                forNode.add(player);
            }
        });

        return forNode;
    }

    @Override
    public Set<IChatPlayer> getAll() {
        return players.values().stream().collect(Collectors.toSet());
    }

    @Override
    public IChatPlayer get(UUID playerId) {
        return players.get(playerId);
    }

    @Override
    public boolean unload(IChatPlayer player) {
        return players.remove(player.getMojangId()) != null;
    }

    @Override
    public boolean load(IChatPlayer player) {
        if (player instanceof ChatPlayer) {
            ChatPlayer cp = (ChatPlayer) player;

            return players.put(player.getMojangId(), cp) == null;
        } else {
            throw new RuntimeException("IChatPlayer must be an instance of ChatPlayer");
        }
    }

}
