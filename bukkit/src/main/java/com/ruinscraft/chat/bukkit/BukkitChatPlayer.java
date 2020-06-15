package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitChatPlayer extends ChatPlayer {

    private final Player handle;

    public BukkitChatPlayer(UUID mojangId, UUID nodeId) {
        super(mojangId, nodeId);
        handle = Bukkit.getPlayer(mojangId);
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
    public void sendMessage(String content) {
        handle.sendMessage(content);
    }

    @Override
    public void openChatMenu() {
        // TODO:
    }

    @Override
    public void openChatSpyMenu() {
        // TODO:
    }

}
