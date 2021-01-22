package com.ruinscraft.chat.util;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public final class VaultUtil {

    private static Permission perms;
    private static Chat chat;

    public static void init() {
        RegisteredServiceProvider<Permission> permsRsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        perms = permsRsp.getProvider();

        RegisteredServiceProvider<Chat> chatRsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        chat = chatRsp.getProvider();
    }

    public static String getGroup(Player player) {
        return perms.getPrimaryGroup(player);
    }

    public static String getPrefix(Player player) {
        return ChatColor.translateAlternateColorCodes('&', chat.getPlayerPrefix(player));
    }

}
