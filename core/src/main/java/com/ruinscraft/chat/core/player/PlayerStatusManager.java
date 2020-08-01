package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.api.IPlayerStatus;
import com.ruinscraft.chat.api.IPlayerStatusManager;
import com.ruinscraft.chat.core.Chat;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PlayerStatusManager implements IPlayerStatusManager {

    private Chat chat;
    private PlayerStatusUpdateTask updateTask;
    private Map<String, Set<IPlayerStatus>> cache;

    public PlayerStatusManager(Chat chat) {
        this.chat = chat;
        updateTask = new PlayerStatusUpdateTask();
        cache = new ConcurrentHashMap<>();

        long delayTicks = 0L;
        long periodTicks = 20 * 1L; // Too short?

        chat.getPlatform().runAsyncRepeat(updateTask, delayTicks, periodTicks);
    }

    @Override
    public Set<IPlayerStatus> getPlayerStatuses(String gamemode) {
        return cache.get(gamemode);
    }

    @Override
    public Map<String, Set<IPlayerStatus>> getPlayerStatuses() {
        return cache;
    }

    @Override
    public boolean isOnline(String username) {
        return getGameMode(username) != null;
    }

    @Override
    public String getGameMode(String username) {
        for (String gamemode : cache.keySet()) {
            for (IPlayerStatus status : cache.get(gamemode)) {
                if (status.getUsername().equalsIgnoreCase(username)) {
                    return gamemode;
                }
            }
        }

        return null;
    }

    private class PlayerStatusUpdateTask implements Runnable {
        @Override
        public void run() {
            IChatStorage storage = chat.getStorage();

            Map<String, Set<IPlayerStatus>> cache = new ConcurrentHashMap<>();

            storage.queryStatuses().thenAccept(statuses -> {
                for (String gamemode : statuses.keySet()) {
                    Set<IPlayerStatus> online = statuses.get(gamemode)
                            .stream()
                            .filter(status -> status.isOnline())
                            .collect(Collectors.toSet());

                    cache.put(gamemode, online);
                }

                PlayerStatusManager.this.cache = cache;
            });
        }
    }

}
