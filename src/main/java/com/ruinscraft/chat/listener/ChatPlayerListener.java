package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatPlayerListener implements Listener {

    private ChatPlugin chatPlugin;

    public ChatPlayerListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onChatPlayerLogin(ChatPlayerLoginEvent event) {
        OnlineChatPlayer loggedIn = event.getOnlineChatPlayer();

        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (chatPlayer instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) chatPlayer;

                if (onlineChatPlayer.isFriend(loggedIn)) {
                    player.sendMessage(ChatColor.GOLD + "Your friend, "
                            + loggedIn.getMinecraftUsername() + ", has logged into " + onlineChatPlayer.getServerName());
                }
            }
        }
    }

    @EventHandler
    public void onChatPlayerLogout(ChatPlayerLogoutEvent event) {
        OnlineChatPlayer loggedOut = event.getOnlineChatPlayer();

        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (chatPlayer instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) chatPlayer;

                if (onlineChatPlayer.isFriend(loggedOut)) {
                    player.sendMessage(ChatColor.GOLD + "Your friend, "
                            + loggedOut.getMinecraftUsername() + ", has logged out ");
                }
            }
        }
    }

}
