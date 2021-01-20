package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.friend.FriendRequest;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FetchFriendRequestTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchFriendRequestTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (Player player : chatPlugin.getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().get(player);

            chatPlugin.getChatStorage().queryFriendRequests(player.getUniqueId()).thenAcceptAsync(friendRequestQuery -> {
                boolean newFriend = chatPlayer.setFriends(friendRequestQuery.getResults());

                if (newFriend) {
                    List<FriendRequest> unacceptedFriends = chatPlayer.getUnacceptedFriends();
                    List<String> unacceptedUsernames = new ArrayList<>();

                    for (FriendRequest unacceptedFriend : unacceptedFriends) {
                        ChatPlayer friendChatPlayer = unacceptedFriend.getFriend(chatPlugin, player.getUniqueId()).join();
                        unacceptedUsernames.add(friendChatPlayer.getMinecraftUsername());
                    }

                    if (player.isOnline()) {
                        player.sendMessage(ChatColor.GOLD + "You have unaccepted friend requests from: " + String.join(", ", unacceptedUsernames));
                        player.sendMessage(ChatColor.GOLD + "Use /friend <accept/deny> <username>");
                    }
                }
            });
        }
    }

}
