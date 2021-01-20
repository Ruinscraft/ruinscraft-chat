package com.ruinscraft.chat.task;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class FetchFriendRequestTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchFriendRequestTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        for (OnlineChatPlayer onlineChatPlayer : chatPlugin.getChatPlayerManager().getOnlineChatPlayers()) {
            chatPlugin.getChatStorage().queryFriendRequests(onlineChatPlayer).thenAccept(friendRequestQuery -> {
                boolean newFriendRequests = onlineChatPlayer.setFriendRequests(friendRequestQuery.getResults());

                if (newFriendRequests) {
                    List<String> friendRequestUsernames = new ArrayList<>();

                    for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
                        if (!friendRequest.isAccepted()) {
                            if (!friendRequest.getRequester().equals(onlineChatPlayer)) {
                                friendRequestUsernames.add(friendRequest.getRequester().getMinecraftUsername());
                            }
                        }
                    }

                    if (!friendRequestUsernames.isEmpty()) {
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "You have new friend requests from: "
                                + ChatColor.AQUA + String.join(", ", friendRequestUsernames));
                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "Use " + ChatColor.AQUA + "/friend <accept/deny> <username>");
                    }
                }
            });
        }
    }

}
