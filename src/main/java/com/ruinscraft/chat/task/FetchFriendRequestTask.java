package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
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

            if (chatPlayer instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) chatPlayer;

                chatPlugin.getChatStorage().queryFriendRequests(onlineChatPlayer).thenAccept(friendRequestQuery -> {
                    boolean newFriendRequests = onlineChatPlayer.setFriendRequests(friendRequestQuery.getResults());

                    if (newFriendRequests) {
                        List<String> friendRequestUsernames = new ArrayList<>();

                        for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
                            friendRequestUsernames.add(friendRequest.getOther(chatPlayer).getMinecraftUsername());
                        }

                        if (player.isOnline()) {
                            player.sendMessage(ChatColor.GOLD + "You have new friend requests from: "
                                    + ChatColor.AQUA + String.join(", ", friendRequestUsernames));
                            player.sendMessage(ChatColor.GOLD + "Use " + ChatColor.AQUA + "/friend <accept/deny> <username>");
                        }
                    }
                });
            }
        }
    }

}
