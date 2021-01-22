package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListCommand implements CommandExecutor {

    private ChatPlugin chatPlugin;

    public ListCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
        chatPlugin.getServer().getScheduler().runTaskTimer(chatPlugin, new UpdateListCache(), 20L, 20L);
    }

    private List<OnlineChatPlayer> staffCache;
    private Map<String, List<OnlineChatPlayer>> serversCache;

    private class UpdateListCache implements Runnable {
        @Override
        public void run() {
            List<OnlineChatPlayer> staff = new ArrayList<>();
            Map<String, List<OnlineChatPlayer>> servers = new HashMap<>();

            for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
                Player player = chatPlugin.getServer().getPlayer(onlineChatPlayer.getMojangId());

                if (player != null && player.isOnline()) {
                    String listName = getColorForGroup(onlineChatPlayer.getGroupName()) + player.getName();
                    player.setPlayerListName(listName);
                }

                if (isStaff(onlineChatPlayer.getGroupName())) {
                    staff.add(onlineChatPlayer);
                }

                if (!servers.containsKey(onlineChatPlayer.getServerName())) {
                    servers.put(onlineChatPlayer.getServerName(), new ArrayList<>());
                }

                servers.get(onlineChatPlayer.getServerName()).add(onlineChatPlayer);
            }

            staffCache = staff;
            serversCache = servers;
        }
    }

    private boolean isStaff(String group) {
        group = group.toLowerCase();

        if (group.contains("administrator") ||
                group.contains("moderator") ||
                group.contains("helper")) {
            return true;
        } else {
            return false;
        }
    }

    private ChatColor getColorForGroup(String group) {
        group = group.toLowerCase();

        if (group.contains("administrator")) {
            return ChatColor.GOLD;
        } else if (group.contains("moderator")) {
            return ChatColor.DARK_BLUE;
        } else if (group.contains("helper")) {
            return ChatColor.AQUA;
        } else if (group.contains("sponsor")) {
            return ChatColor.DARK_PURPLE;
        } else if (group.contains("builder")) {
            return ChatColor.GREEN;
        } else {
            return ChatColor.GRAY;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean localServerOnly = true;

        if (label.toLowerCase().contains("all") || label.toLowerCase().contains("everyone")) {
            localServerOnly = false;
        }

        int totalPlayers = 0;

        for (String server : serversCache.keySet()) {
            if (localServerOnly) {
                if (!server.equals(ChatPlugin.serverName)) {
                    continue;
                }
            }

            List<OnlineChatPlayer> onlineChatPlayers = serversCache.get(server);
            List<String> onlineNames = new ArrayList<>();

            for (OnlineChatPlayer onlineChatPlayer : onlineChatPlayers) {
                if (sender.hasPermission("ruinscraft.list.showvanished") && onlineChatPlayer.isVanished()) {
                    continue;
                }
                onlineNames.add(getColorForGroup(onlineChatPlayer.getGroupName()) + onlineChatPlayer.getMinecraftUsername());
                totalPlayers++;
            }

            sender.sendMessage(ChatColor.GOLD + server.toUpperCase() + ChatColor.YELLOW + " (" + onlineNames.size() + ") " + ChatColor.RESET + String.join(", ", onlineNames));
        }

        List<String> staffNames = new ArrayList<>();

        for (OnlineChatPlayer staff : staffCache) {
            if (sender.hasPermission("ruinscraft.list.showvanished") && staff.isVanished()) {
                continue;
            }

            staffNames.add(getColorForGroup(staff.getGroupName()) + staff.getMinecraftUsername() + ChatColor.GRAY + " (" + staff.getServerName().toUpperCase() + ")" + ChatColor.RESET);
        }

        sender.sendMessage(ChatColor.GREEN + "Staff Online: " + ChatColor.YELLOW + "(" + staffNames.size() + ") " + String.join(", ", staffNames));

        if (sender instanceof Player) {
            Player player = (Player) sender;
            OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
            List<String> onlineFriendNames = new ArrayList<>();

            for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
                ChatPlayer friend = friendRequest.getOther(onlineChatPlayer);

                if (friend instanceof OnlineChatPlayer) {
                    OnlineChatPlayer onlineFriend = (OnlineChatPlayer) friend;
                    onlineFriendNames.add(getColorForGroup(onlineFriend.getGroupName()) + onlineFriend.getMinecraftUsername() + ChatColor.GRAY + " (" + onlineFriend.getServerName().toUpperCase() + ")" + ChatColor.RESET);
                }
            }

            player.sendMessage(ChatColor.GREEN + "Friends Online: " + ChatColor.YELLOW + "(" + onlineFriendNames.size() + ") " + String.join(", ", onlineFriendNames));
        }

        if (localServerOnly) {
            sender.sendMessage(ChatColor.GOLD + "Total players on " + ChatPlugin.serverName.toUpperCase() + ": " + ChatColor.YELLOW + totalPlayers);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Total players on Ruinscraft: " + ChatColor.YELLOW + totalPlayers);
        }

        return true;
    }

}
