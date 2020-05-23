package com.ruinscraft.chat.core.messagebroker;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.messagebroker.IMessage;
import com.ruinscraft.chat.api.messagebroker.IMessageBroker;

public abstract class MessageBroker implements IMessageBroker {

    @Override
    public void publish(IMessage message) {
        String serialized = message.serialize();
    }

    @Override
    public void consume(IMessage message) {
        IChatMessage content = message.getContent();
    }

}
