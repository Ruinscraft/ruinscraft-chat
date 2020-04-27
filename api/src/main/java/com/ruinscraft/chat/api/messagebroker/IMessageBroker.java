package com.ruinscraft.chat.api.messagebroker;

public interface IMessageBroker {

    void publish(IMessage message);

    void consume(IMessage message);

    void close();

}
