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
                    acceptFriend(player, args[1]);
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

    private void listFriends(Player player) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        boolean empty = true;

        for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
            if (friendRequest.isAccepted()) {
                empty = false;
                player.sendMessage(ChatColor.GOLD + friendRequest.getOther(onlineChatPlayer).getMinecraftUsername());
            }
        }

        if (empty) {
            player.sendMessage(ChatColor.GOLD + "You have no friends added.");
        }
    }

    private void addFriend(Player player, String target) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (onlineChatPlayer.isFriend(targetChatPlayer)) {
                    onlineChatPlayer.sendMessage(ChatColor.RED + targetChatPlayer.getMinecraftUsername() + " is already your friend.");
                } else {
                    FriendRequest friendRequest = new FriendRequest(onlineChatPlayer, targetChatPlayer, System.currentTimeMillis(), false);

                    chatPlugin.getChatStorage().saveFriendRequest(friendRequest).thenRun(() -> {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "Friend request sent to: " + targetChatPlayer.getMinecraftUsername());
                    });
                }
            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    public void removeFriend(Player player, String target) {

    }

    private void acceptFriend(Player player, String target) {
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        chatPlugin.getChatStorage().queryChatPlayer(target).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer targetChatPlayer = chatPlayerQuery.getFirst();

                if (onlineChatPlayer.isFriendRequestPending(targetChatPlayer)) {
                    FriendRequest friendRequest = onlineChatPlayer.getFriendRequest(targetChatPlayer);
                    friendRequest.setAccepted(true);

                    chatPlugin.getChatStorage().saveFriendRequest(friendRequest).thenRun(() -> {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "You are now friends with " + targetChatPlayer.getMinecraftUsername() + "!");
                    });
                } else {
                    onlineChatPlayer.sendMessage(ChatColor.RED
                            + "You do not have a pending friend request from " + targetChatPlayer.getMinecraftUsername());
                }

            } else {
                onlineChatPlayer.sendMessage(ChatColor.RED + target + " has never joined before.");
            }
        });
    }

    private void denyFriend(Player player, String target) {

    }

}
