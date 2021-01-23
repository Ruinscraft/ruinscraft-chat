package com.ruinscraft.chat.util;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class PlayerDataImporter {

    public static void doImport(ChatPlugin chatPlugin) {
        int current = 0;
        int total = Bukkit.getOfflinePlayers().length;

        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            System.out.println("Importing " + (current++) + " of " + total);

            UUID mojangId = offlinePlayer.getUniqueId();
            String username = offlinePlayer.getName();
            long firstSeen = offlinePlayer.getFirstPlayed();
            long lastSeen = offlinePlayer.getLastPlayed();
            ChatPlayer chatPlayer = new ChatPlayer(mojangId, username, firstSeen, lastSeen);

            chatPlugin.getChatStorage().queryChatPlayer(mojangId).thenAccept(chatPlayerQuery -> {
                if (chatPlayerQuery.hasResults()) {
                    ChatPlayer found = chatPlayerQuery.getFirst();

                    if (found.getFirstSeen() < firstSeen) {
                        chatPlayer.setFirstSeen(found.getFirstSeen());
                    }

                    if (found.getLastSeen() > lastSeen) {
                        chatPlayer.setLastSeen(found.getLastSeen());
                    }

                    if (chatPlayer.getMinecraftUsername() == null) {
                        chatPlayer.setMinecraftUsername(found.getMinecraftUsername());
                    }
                }
            }).thenRun(() -> {
                chatPlugin.getChatStorage().saveChatPlayer(chatPlayer);
            });
        }
    }

}
