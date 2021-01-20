package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.friend.FriendRequest;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.storage.query.ChatPlayerQuery;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FriendCommand implements CommandExecutor {

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
                    break;
                case "accept":
                    break;
                case "deny":
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

    private CompletableFuture<Void> listFriends(Player player) {
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

        return CompletableFuture.runAsync(() -> {
            List<ChatPlayer> friends = new ArrayList<>();

            for (FriendRequest friendRequest : chatPlayer.getAcceptedFriends()) {
                friends.add(friendRequest.getFriend(chatPlugin, player.getUniqueId()).join());
            }

            if (friends.isEmpty()) {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GOLD + "You have no friends added.");
                }
                return;
            }

            for (ChatPlayer friend : friends) {
                if (player.isOnline()) {
                    player.sendMessage(friend.getMinecraftUsername() + " is your friend");
                }
            }
        });
    }

    private CompletableFuture<Void> addFriend(Player player, String target) {
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

        return CompletableFuture.runAsync(() -> {
            ChatPlayerQuery chatPlayerQuery = chatPlugin.getChatStorage().queryChatPlayer(target).join();

            if (!chatPlayerQuery.hasResults()) {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.RED + target + " has never joined before.");
                }
            } else {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (chatPlayer.isFriend(targetChatPlayer)) {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is already your friend.");
                    }
                    return;
                }

                UUID requesterId = player.getUniqueId();
                UUID targetId = targetChatPlayer.getMojangId();
                long time = System.currentTimeMillis();
                boolean accepted = false;
                FriendRequest friendRequest = new FriendRequest(requesterId, targetId, time, accepted);
                chatPlugin.getChatStorage().saveFriendRequest(friendRequest).thenRun(() -> {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + targetChatPlayer.getMinecraftUsername() + " has been sent a friend request.");
                    }
                });
            }
        });
    }

    private CompletableFuture<Void> removeFriend(Player player, String target) {
        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

        return CompletableFuture.runAsync(() -> {
            ChatPlayerQuery chatPlayerQuery = chatPlugin.getChatStorage().queryChatPlayer(target).join();

            if (!chatPlayerQuery.hasResults()) {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.RED + target + " has never joined before.");
                }
            } else {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (!chatPlayer.isFriend(targetChatPlayer)) {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is not your friend.");
                    }
                    return;
                }

                FriendRequest friendRequest = chatPlayer.getFriendRequest(targetChatPlayer);

                chatPlugin.getChatStorage().deleteFriendRequest(friendRequest).thenRun(() -> {
                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + targetChatPlayer.getMinecraftUsername() + " is no longer your friend.");
                    }
                });
            }
        });
    }

    private void acceptFriend(Player player, String target) {

    }

    private void denyFriend(Player player, String target) {

    }

}
