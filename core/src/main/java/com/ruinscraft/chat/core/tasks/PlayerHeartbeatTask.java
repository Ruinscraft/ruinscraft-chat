package com.ruinscraft.chat.core.tasks;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.core.ChatPlatform;
import com.ruinscraft.chat.core.player.ChatPlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerHeartbeatTask implements Runnable {

    private ChatPlatform platform;

    public PlayerHeartbeatTask(ChatPlatform platform) {
        this.platform = platform;
    }

    @Override
    public void run() {
        IChat chat = platform.getChat();
        UUID nodeId = chat.getNodeId();

        // collect information
        Set<IChatPlayer> previouslyOnline = chat.getOnlinePlayers().getForNode(nodeId);
        Set<UUID> currentlyOnlineIds = platform.getOnlinePlayers();

        // first, purge offline players
        purgeOffline(previouslyOnline, currentlyOnlineIds);

        // then, add new online players
        addOnline(previouslyOnline, currentlyOnlineIds);

        // finally, save players which need saving (for current node)
        savePlayers();
    }

    private void purgeOffline(Set<IChatPlayer> previouslyOnline, Set<UUID> currentlyOnlineIds) {
        Set<IChatPlayer> toPurge = new HashSet<>();
        // check previously online versus currently online
        for (IChatPlayer cp : previouslyOnline) {
            if (!currentlyOnlineIds.contains(cp.getMojangId())) {
                // we found an offline player
                toPurge.add(cp);
            }
        }

        if (!toPurge.isEmpty()) {
            platform.getChat().getStorage().purgeOfflinePlayers(toPurge);
        }
    }

    private void addOnline(Set<IChatPlayer> previouslyOnline, Set<UUID> currentlyOnlineIds) {
        Set<UUID> toAdd = new HashSet<>();

        // check currently online ids versus previously online chat players
        for (UUID mojangId : currentlyOnlineIds) {
            long matches = previouslyOnline.stream().filter(cp -> cp.getMojangId().equals(mojangId)).count();

            if (matches == 0) {
                // new player has joined which needs to be loaded
                IChatPlayer chatPlayer = platform.createChatPlayer(mojangId);

                platform.getChat().getStorage().loadPlayer(chatPlayer);
            }
        }


    }

    private void savePlayers() {
        IChat chat = platform.getChat();
        UUID nodeId = chat.getNodeId();

        chat.getOnlinePlayers().getForNode(nodeId).forEach(icp -> {
            if (icp instanceof ChatPlayer) {
                ChatPlayer cp = (ChatPlayer) icp;

                if (cp.requiresSave()) {
                    chat.getStorage().savePlayer(cp);
                }
            }
        });
    }

}
