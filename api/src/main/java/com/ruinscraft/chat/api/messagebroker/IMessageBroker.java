package com.ruinscraft.chat.api.messagebroker;

public interface IMessageBroker<T extends IMessage> {

    void publish(T message);

    void consume(T message);

    void close();

}
