package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IOnlinePlayers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OnlinePlayers implements IOnlinePlayers {

    private Map<UUID, List<String>> onlinePlayers;

    @Override
    public List<String> getPlayers(UUID nodeId) {
        return onlinePlayers.get(nodeId);
    }

    @Override
    public void setPlayers(UUID nodeId, List<String> players) {
        onlinePlayers.put(nodeId, players);
    }

}
