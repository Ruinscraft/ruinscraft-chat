package com.ruinscraft.chat.core.command;

import com.ruinscraft.chat.api.IChatCommandExec;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.core.Chat;

import java.util.Set;

public class ListCommand implements IChatCommandExec {

    private Chat chat;

    public ListCommand(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void onExecute(IChatPlayer executor, String label, String[] args) {
        Set<IChatPlayer> players = chat.getOnlinePlayers().getAll();

        for (IChatPlayer player : players) {
            executor.sendMessage(player.getDisplayName());
        }

        executor.sendMessage("Total: " + players.size());
    }

}
