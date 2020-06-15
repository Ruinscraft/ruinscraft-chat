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
        System.out.println("Running heartbeat task");

        IChat chat = platform.getChat();
        UUID nodeId = chat.getNodeId();

        // collect information
        Set<IChatPlayer> previouslyOnline = chat.getOnlinePlayers().getForNode(nodeId);
        Set<UUID> currentlyOnlineIds = platform.getOnlineIds();

        // first, purge offline players
        purgeOffline(previouslyOnline, currentlyOnlineIds);

        // then, add new online players
        addOnline(previouslyOnline, currentlyOnlineIds);

        // finally, save players which need saving (for current node)
        savePlayers();
    }

    private void purgeOffline(Set<IChatPlayer> previouslyOnline, Set<UUID> currentlyOnlineIds) {
        Set<IChatPlayer> toPurge = new HashSet<>();
        IChat chat = platform.getChat();

        // check previously online versus currently online
        for (IChatPlayer player : previouslyOnline) {
            if (!currentlyOnlineIds.contains(player.getMojangId())) {
                // we found an offline player
                toPurge.add(player);
                chat.getOnlinePlayers().unload(player);
            }
        }

        // purge from database
        // TODO: move this?
        if (!toPurge.isEmpty()) {
            chat.getStorage().purgeOfflinePlayers(toPurge);
        }
    }

    private void addOnline(Set<IChatPlayer> previouslyOnline, Set<UUID> currentlyOnlineIds) {
        // check currently online ids versus previously online chat players
        for (UUID mojangId : currentlyOnlineIds) {
            long matches = previouslyOnline.stream().filter(cp -> cp.getMojangId().equals(mojangId)).count();

            if (matches == 0) {
                // new player has joined which needs to be loaded
                IChatPlayer chatPlayer = platform.createChatPlayer(mojangId);
                IChat chat = platform.getChat();

                chat.getStorage().loadPlayer(chatPlayer);
                chat.getOnlinePlayers().load(chatPlayer);
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
