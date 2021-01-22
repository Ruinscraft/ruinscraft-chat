package com.ruinscraft.chat.util;

import com.ruinscraft.chat.ChatPlugin;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SpamHandler {

    private Map<UUID, PriorityQueue<Long>> recentMessages;

    public SpamHandler(ChatPlugin chatPlugin) {
        recentMessages = new ConcurrentHashMap<>();

        chatPlugin.getServer().getScheduler().runTaskTimer(chatPlugin, () -> {
            for (UUID playerId : recentMessages.keySet()) {
                PriorityQueue<Long> priorityQueue = recentMessages.get(playerId);

                if (priorityQueue.isEmpty()) {
                    recentMessages.remove(playerId);
                } else {
                    priorityQueue.poll();
                }
            }
        }, 20L, 3 * 20L);
    }

    public void addMessage(UUID playerId) {
        if (!recentMessages.containsKey(playerId)) {
            recentMessages.put(playerId, new PriorityQueue<>());
        }

        PriorityQueue<Long> priorityQueue = recentMessages.get(playerId);

        if (priorityQueue.size() < 8) {
            priorityQueue.add(System.currentTimeMillis());
        }
    }

    public boolean canSendMessage(UUID playerId) {
        if (!recentMessages.containsKey(playerId)) {
            return true;
        }

        PriorityQueue<Long> priorityQueue = recentMessages.get(playerId);

        if (priorityQueue.isEmpty()) {
            return true;
        }

        if (priorityQueue.size() > 4) {
            return false;
        }

        return true;
    }

}
