package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.entity.Player;

public class BukkitChatPlayer extends ChatPlayer {

    private Player handle;

    public BukkitChatPlayer(Player handle) {
        super(handle.getUniqueId());
        this.handle = handle;
    }

    @Override
    public String getUsername() {
        return handle.getName();
    }

    @Override
    public String getDisplayName() {
        return handle.getDisplayName();
    }

    @Override
    public boolean hasPermission(String permission) {
        return handle.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        handle.sendMessage(message);
    }

}
