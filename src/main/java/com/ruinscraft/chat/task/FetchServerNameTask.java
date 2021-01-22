package com.ruinscraft.chat.task;

import com.google.common.collect.Iterables;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.util.NetworkUtil;
import org.bukkit.entity.Player;

public class FetchServerNameTask implements Runnable {

    private ChatPlugin chatPlugin;

    public FetchServerNameTask(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void run() {
        if (ChatPlugin.serverName == null) {
            if (!chatPlugin.getServer().getOnlinePlayers().isEmpty()) {
                Player first = Iterables.getFirst(chatPlugin.getServer().getOnlinePlayers(), null);
                if (first != null) {
                    NetworkUtil.sendServerNameRequestBungeePacket(first, chatPlugin);
                }
            }
        }
    }

}
