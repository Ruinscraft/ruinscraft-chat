package com.ruinscraft.chat.bukkit;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultUtil {

    private static net.milkbowl.vault.chat.Chat vChat;

    public static void init() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        vChat = rsp.getProvider();
    }

    public static void updatePlayerDisplayName(Player player) {
        if (vChat != null) {
            String displayName = vChat.getPlayerPrefix(player) + player.getName() + "&r";

            displayName = ChatColor.translateAlternateColorCodes('&', displayName);

            player.setDisplayName(displayName);
        }
    }

}
