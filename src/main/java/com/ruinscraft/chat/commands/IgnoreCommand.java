package com.ruinscraft.chat.commands;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.players.ChatPlayer;
import com.ruinscraft.chat.players.MinecraftIdentity;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * A command for Players to ignore and unignore certain players in chat.
 */
public class IgnoreCommand implements CommandExecutor {

    private static final ChatPlugin chatPlugin = ChatPlugin.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getChatPlayer(player.getUniqueId());

        /* Supports usernames or UUIDs */
        if (args.length > 0) {
            String arg0 = args[0];

            if (arg0.equalsIgnoreCase(player.getName())) {
                player.sendMessage(Constants.COLOR_BASE + "You can't ignore yourself");
                return true;
            }

            MinecraftIdentity minecraftIdentity = null;
            OfflinePlayer potentialOfflinePlayer = Bukkit.getOfflinePlayer(arg0);

            if (potentialOfflinePlayer.hasPlayedBefore()) {
                minecraftIdentity = new MinecraftIdentity(potentialOfflinePlayer.getUniqueId().toString());
            } else {
                minecraftIdentity = new MinecraftIdentity(arg0);
            }

            if (chatPlayer.ignore(minecraftIdentity)) {
                player.sendMessage(Constants.COLOR_BASE + "Ignored " + Constants.COLOR_ACCENT + minecraftIdentity.getIdentity());
            } else {
                chatPlayer.unignore(minecraftIdentity);
                player.sendMessage(Constants.COLOR_BASE + "Unignored " + Constants.COLOR_ACCENT + minecraftIdentity.getIdentity());
            }

            return true;
        }

        if (chatPlayer.ignoring.isEmpty()) {
            player.sendMessage(Constants.COLOR_BASE + "You are not ignoring anyone");
            return true;
        }

        player.sendMessage(Constants.COLOR_BASE + "Currently Ignoring:");

        for (MinecraftIdentity minecraftIdentity : chatPlayer.ignoring) {
            String name = "?";

            if (minecraftIdentity.isUUID()) {
                UUID uuid = UUID.fromString(minecraftIdentity.getIdentity());

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                if (offlinePlayer.getName() != null) {
                    name = offlinePlayer.getName();
                } else {
                    name = uuid.toString();
                }
            } else {
                name = minecraftIdentity.getIdentity();
            }

            player.sendMessage(Constants.COLOR_ACCENT + name);
        }

        return true;
    }

}
