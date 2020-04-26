package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IMessageFormatter;
import com.ruinscraft.chat.core.message.DefaultMessageFormatter;

public class DefaultChatChannel extends ChatChannel {

    private IMessageFormatter formatter;

    public DefaultChatChannel() {
        super("default", "The default chat channel");
        formatter = new DefaultMessageFormatter();
    }

    @Override
    public IMessageFormatter getFormatter() {
        return formatter;
    }

}
