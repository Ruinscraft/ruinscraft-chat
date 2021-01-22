package com.ruinscraft.chat.util;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class SpamHandler {

    private Map<ChatPlayer, PriorityQueue<Long>> recentMessages;

    public SpamHandler(ChatPlugin chatPlugin) {
        recentMessages = new ConcurrentHashMap<>();

        chatPlugin.getServer().getScheduler().runTaskTimer(chatPlugin, () -> {
            for (ChatPlayer chatPlayer : recentMessages.keySet()) {
                PriorityQueue<Long> priorityQueue = recentMessages.get(chatPlayer);

                if (priorityQueue.isEmpty()) {
                    recentMessages.remove(chatPlayer);
                } else {
                    priorityQueue.poll();
                }
            }
        }, 20L, 5 * 20L);
    }

    public boolean canSendMessage(ChatPlayer chatPlayer) {
        if (!recentMessages.containsKey(chatPlayer)) {
            PriorityQueue priorityQueue = new PriorityQueue();
            priorityQueue.add(System.currentTimeMillis());
            recentMessages.put(chatPlayer, priorityQueue);
        }

        PriorityQueue<Long> priorityQueue = recentMessages.get(chatPlayer);

        if (priorityQueue.isEmpty()) {
            return true;
        }

        if (priorityQueue.size() > 3) {
            return false;
        }

        priorityQueue.add(System.currentTimeMillis());

        return true;
    }

}
