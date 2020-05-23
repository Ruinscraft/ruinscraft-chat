package com.ruinscraft.chat.api;

import java.util.List;
import java.util.UUID;

public interface IOnlinePlayers {

    List<String> getPlayers(UUID nodeId);

    void setPlayers(UUID nodeId, List<String> players);

}
