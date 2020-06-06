package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitChatPlayer extends ChatPlayer {

    private Player handle;

    public BukkitChatPlayer(UUID mojangId) {
        super(mojangId);
        handle = Bukkit.getPlayer(mojangId);
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public void sendMessage(IChatMessage message) {



    }

}
