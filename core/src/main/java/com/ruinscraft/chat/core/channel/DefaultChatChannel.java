package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IMessageFormatter;
import com.ruinscraft.chat.core.message.DefaultMessageFormatter;

import java.util.Collection;

public class DefaultChatChannel extends ChatChannel {

    private IMessageFormatter formatter;

    public DefaultChatChannel() {
        super("default");
        formatter = new DefaultMessageFormatter();
    }

    @Override
    public IMessageFormatter getFormatter() {
        return formatter;
    }

    @Override
    public Collection<IChatPlayer> getRecipients() {
        return null;
    }

}
