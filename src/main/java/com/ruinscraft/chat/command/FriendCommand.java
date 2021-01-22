package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.command.completers.ChatPlayersTabCompleter;
import com.ruinscraft.chat.command.completers.FriendsCompleter;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class FriendCommand implements CommandExecutor, TabCompleter {

    private ChatPlugin chatPlugin;

    public FriendCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            showHelp(player, label);
        } else if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    listFriends(player);
                    break;
                default:
                    showHelp(player, label);
                    break;
            }
        } else if (args.length > 1) {
            switch (args[0].toLowerCase()) {
                case "add":
                    addFriend(player, args[1]);
                    break;
                case "remove":
                    removeFriend(player, args[1]);
                    break;
                case "accept":
                    acceptFriend(player, args[1]);
                    break;
                case "deny":
                    denyFriend(player, args[1]);
                    break;
                default:
                    showHelp(player, label);
                    break;
            }
        }

        return true;
    }

    private void showHelp(Player player, String label) {
        player.sendMessage(ChatColor.RED + "/" + label + " <list, add, remove, accept, deny> [username]");
    }

    private void listFriends(Player player) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        boolean empty = true;

        onlineChatPlayer.sendMessage(ChatColor.GOLD + "====== Your Friends ======");

        for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
            if (friendRequest.isAccepted()) {
                empty = false;

                StringJoiner stringJoiner = new StringJoiner(" ");
                ChatPlayer friend = friendRequest.getOther(onlineChatPlayer);

                if (friend instanceof OnlineChatPlayer) {
                    OnlineChatPlayer onlineFriend = (OnlineChatPlayer) friend;
                    stringJoiner.add(ChatColor.GREEN + onlineFriend.getMinecraftUsername());
                    stringJoiner.add(ChatColor.YELLOW + "is currently online playing " + onlineFriend.getServerName().toUpperCase() + "!");
                } else {
                    stringJoiner.add(ChatColor.GRAY + friend.getMinecraftUsername());
                    stringJoiner.add(ChatColor.YELLOW + "was last online");
                    stringJoiner.add(friend.getLastSeenDurationWords());
                    stringJoiner.add("ago");
                }

                onlineChatPlayer.sendMessage(stringJoiner.toString());
            }
        }

        if (empty) {
            player.sendMessage(ChatColor.GOLD + "You have no friends added.");
        }
    }

    private void addFriend(Player player, String target) {
        if (player.getName().equalsIgnoreCase(target)) {
            player.sendMessage(ChatColor.RED + "You can't add yourself as a friend. Sorry.");
            return;
        }

        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (onlineChatPlayer.isFriend(targetChatPlayer)) {
                    onlineChatPlayer.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is already your friend.");
                } else {
                    FriendRequest friendRequest = new FriendRequest(onlineChatPlayer, targetChatPlayer, System.currentTimeMillis(), false);

                    chatPlugin.getChatStorage().saveFriendRequest(friendRequest).thenRun(() -> {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "Friend request sent to " + targetChatPlayer.getMinecraftUsername());
                    });
                }
            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    public void removeFriend(Player player, String target) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (onlineChatPlayer.isFriend(targetChatPlayer)) {
                    FriendRequest friendRequest = onlineChatPlayer.removeFriendRequest(targetChatPlayer);

                    chatPlugin.getChatStorage().deleteFriendRequest(friendRequest).thenRun(() -> {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + targetChatPlayer.getMinecraftUsername() + " has been removed from your friends list.");
                    });
                } else {
                    onlineChatPlayer.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is not your friend.");
                }
            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    private void acceptFriend(Player player, String target) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();
                FriendRequest friendRequest = onlineChatPlayer.getFriendRequest(targetChatPlayer);

                if (friendRequest == null) {
                    onlineChatPlayer.sendMessage(ChatColor.GOLD + "You do not have a friend request from " + targetChatPlayer.getMinecraftUsername());
                } else {
                    if (friendRequest.isAccepted()) {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + friendRequest.getOther(onlineChatPlayer).getMinecraftUsername() + " is already your friend.");
                    } else {
                        friendRequest.setAccepted(true);

                        chatPlugin.getChatStorage().saveFriendRequest(friendRequest).thenRun(() -> {
                            onlineChatPlayer.sendMessage(ChatColor.GOLD + "You are now friends with " + targetChatPlayer.getMinecraftUsername() + "!");
                        });
                    }
                }
            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    private void denyFriend(Player player, String target) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();
                FriendRequest friendRequest = onlineChatPlayer.getFriendRequest(targetChatPlayer);

                if (friendRequest == null || !friendRequest.isAccepted()) {
                    onlineChatPlayer.sendMessage(ChatColor.RED + "You do not have a friend request from " + targetChatPlayer.getMinecraftUsername());
                } else {
                    onlineChatPlayer.removeFriendRequest(targetChatPlayer);

                    chatPlugin.getChatStorage().deleteFriendRequest(friendRequest).thenRun(() -> {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "You've denied " + targetChatPlayer.getMinecraftUsername() + "'s friend request. They will not be notified.");
                    });
                }
            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    private static List<String> options = new ArrayList<>();

    static {
        options.add("list");
        options.add("add");
        options.add("remove");
        options.add("accept");
        options.add("deny");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String option : options) {
                if (option.startsWith(args[0].toLowerCase())) {
                    completions.add(option);
                }
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                ChatPlayersTabCompleter chatPlayersTabCompleter = new ChatPlayersTabCompleter(chatPlugin);
                return chatPlayersTabCompleter.onTabComplete(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("remove")) {
                FriendsCompleter friendsCompleter = new FriendsCompleter(chatPlugin);
                return friendsCompleter.onTabComplete(sender, command, label, args);
            } else if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                List<String> pendingRequestUsernames = new ArrayList<>();
                if (sender instanceof Player) {
                    OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get((Player) sender);
                    for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
                        if (!friendRequest.isAccepted()) {
                            pendingRequestUsernames.add(friendRequest.getOther(onlineChatPlayer).getMinecraftUsername());
                        }
                    }
                }
                return pendingRequestUsernames;
            }
        }

        return completions;
    }

}
