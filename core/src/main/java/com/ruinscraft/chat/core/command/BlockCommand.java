package com.ruinscraft.chat.core.command;

import com.ruinscraft.chat.api.IChatCommandExec;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.core.Chat;

import java.util.UUID;

public class BlockCommand implements IChatCommandExec {

    private Chat chat;

    public BlockCommand(Chat chat) {
        this.chat = chat;
    }

    @Override
    public void onExecute(IChatPlayer executor, String label, String[] args) {
        if (args.length < 1) {
            // show currently blocked users

            return;
        }

        String target = args[0];
        UUID targetId = chat.getPlatform().getPlayerId(target);

        if (targetId == null) {
            // target not found
            executor.sendMessage("User not found.");
            return;
        }

        IChatPlayer targetPlayer = chat.getChatPlayer(targetId);

        if (executor.block(targetPlayer)) {
            executor.sendMessage("User blocked.");
        } else {
            executor.sendMessage("You already have this user blocked.");
        }
    }

}
