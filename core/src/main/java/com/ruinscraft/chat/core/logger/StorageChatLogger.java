package com.ruinscraft.chat.core.logger;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.api.IMessageFormatter;

public class StorageChatLogger extends ChatLogger {

    public StorageChatLogger(IChat chat) {
        super(chat);
    }

    @Override
    public IMessageFormatter getFormatter() {
        return null; // N/A
    }

    @Override
    public void log(IChatMessage message) {
        IChatStorage storage = chat.getStorage();

        storage.logMessage(message);
    }

}
