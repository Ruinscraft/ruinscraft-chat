package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.util.FilterUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignContentFilterListener implements Listener {

    private ChatPlugin chatPlugin;

    public SignContentFilterListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        boolean cancel = false;

        for (String line : event.getLines()) {
            if (line != null && !line.isEmpty()) {
                if (FilterUtil.isBadMessage(chatPlugin.getRacialSlurs(), line)) {
                    cancel = true;
                }
            }
        }

        if (cancel) {
            event.setLine(0, "");
            event.setLine(1, "");
            event.setLine(2, "");
            event.setLine(3, "");
        }
    }

}
