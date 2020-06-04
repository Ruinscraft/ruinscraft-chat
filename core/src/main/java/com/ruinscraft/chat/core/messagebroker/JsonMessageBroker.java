package com.ruinscraft.chat.core.messagebroker;

import com.ruinscraft.chat.api.messagebroker.IMessageBroker;

public abstract class JsonMessageBroker implements IMessageBroker<JsonMessage> {

    @Override
    public void publish(JsonMessage message) {
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
    public void consume(JsonMessage message) {
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
