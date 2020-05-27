package com.ruinscraft.chat.core.messagebroker;

import com.ruinscraft.chat.api.messagebroker.IMessage;
import com.ruinscraft.chat.api.messagebroker.IMessageBroker;

public abstract class MessageBroker implements IMessageBroker {

    @Override
    public void publish(IMessage message) {
        switch (message.getType()) {
            case MESSAGE_PLAYER_HEARTBEAT:
                break;
            case MESSAGE_CHAT_MESSAGE:
                break;
            case MESSAGE_VANISH_TOGGLE:
                break;
        }
    }

    @Override
    public void consume(IMessage message) {
        switch (message.getType()) {
            case MESSAGE_PLAYER_HEARTBEAT:
                break;
            case MESSAGE_CHAT_MESSAGE:
                break;
            case MESSAGE_VANISH_TOGGLE:
                break;
        }
    }

}
