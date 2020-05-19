package com.ruinscraft.chat.core.logger;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatLogger;

public abstract class ChatLogger implements IChatLogger {

    protected IChat chat;

    public ChatLogger(IChat chat) {
        this.chat = chat;
    }

}
