package com.ruinscraft.chat.core.command;

import com.ruinscraft.chat.api.IChatCommandExec;
import com.ruinscraft.chat.api.IChatPlayer;

public class ChatSpyCommand implements IChatCommandExec {

    @Override
    public void onExecute(IChatPlayer executor, String label, String[] args) {
        executor.openChatSpyMenu();
    }

}