package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.event.ChatPlayerLogoutEvent;
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
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (onlineChatPlayer.isFriend(loggedIn)) {
                if (!player.hasPermission("ruinscraft.command.vanish") && loggedIn.isVanished()) {
                    continue;
                }

                if (loggedIn.getPersonalizationSettings().isSilentJoinLeave()) {
                    continue;
                }

                player.sendMessage(ChatColor.GOLD + "Your friend, " + loggedIn.getMinecraftUsername()
                        + ", has logged into " + loggedIn.getServerName().toUpperCase() + ".");
            }
        }
    }

    @EventHandler
    public void onChatPlayerLogout(ChatPlayerLogoutEvent event) {
        OnlineChatPlayer loggedOut = event.getOnlineChatPlayer();

        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

            if (onlineChatPlayer.isFriend(loggedOut)) {
                if (!player.hasPermission("ruinscraft.command.vanish") && loggedOut.isVanished()) {
                    continue;
                }

                if (loggedOut.getPersonalizationSettings().isSilentJoinLeave()) {
                    continue;
                }

                player.sendMessage(ChatColor.GOLD + "Your friend, " + loggedOut.getMinecraftUsername() + ", has logged out.");
            }
        }
    }

}
