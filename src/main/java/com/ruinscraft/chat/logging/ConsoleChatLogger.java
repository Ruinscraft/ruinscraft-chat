package com.ruinscraft.chat.logging;

import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

import java.util.concurrent.Callable;

public class ConsoleChatLogger implements ChatLogger {

    private static final String PREFIX = "[CHAT] ";

    @Override
    public Callable<Void> log(ChatMessage message) {
        return new Callable<Void>() {
            @Override
            public Void call() {
                if (message instanceof PrivateChatMessage) {
                    PrivateChatMessage pm = (PrivateChatMessage) message;
                    String format = "[%s -> %s] %s";
                    System.out.println(PREFIX + String.format(format, pm.getSender(), pm.getRecipient(), pm.getPayload()));
                } else {
                    String format = "[%s] [%s] [%s] > %s";
                    System.out.println(PREFIX + String.format(format, message.getServerSentFrom(), message.getIntendedChannelName(), message.getSender(), message.getPayload()));
                }

                return null;
            }
        };

    }

}
