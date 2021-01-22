package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.NetworkUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinQuitListener implements Listener {

    private ChatPlugin chatPlugin;

    public PlayerJoinQuitListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        chatPlugin.getChatPlayerManager().getAndLoad(player);
        NetworkUtil.sendChatPlayerLoginPacket(chatPlugin, player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
        chatPlugin.getChatStorage().saveChatPlayer(onlineChatPlayer);

        NetworkUtil.sendChatPlayerLogoutPacket(chatPlugin, player);
    }

}
