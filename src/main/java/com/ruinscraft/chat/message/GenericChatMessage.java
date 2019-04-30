package com.ruinscraft.chat.message;

import java.util.UUID;

public class GenericChatMessage implements ChatMessage {

    private final String senderPrefix;
    private final String senderNickname;
    private final UUID uuid;
    private final String sender;
    private final String serverSentFrom;
    private final String intendedChannelName;
    private final boolean colorizePayload;
    private String payload;

    public GenericChatMessage(String senderPrefix, String senderNickname, UUID uuid, String sender, String serverSentFrom, String intendedChannelName, boolean colorizePayload, String payload) {
        this.senderPrefix = senderPrefix;
        this.senderNickname = senderNickname;
        this.uuid = uuid;
        this.sender = sender;
        this.serverSentFrom = serverSentFrom;
        this.intendedChannelName = intendedChannelName;
        this.colorizePayload = colorizePayload;
        this.payload = payload;
    }

    @Override
    public String getSenderPrefix() {
        return senderPrefix;
    }

    @Override
    public String getSenderNickname() {
        return senderNickname;
    }

    @Override
    public UUID getSenderUUID() {
        return uuid;
    }

    @Override
    public String getSender() {
        return sender;
    }

    @Override
    public String getServerSentFrom() {
        return serverSentFrom;
    }

    @Override
    public boolean colorizePayload() {
        return colorizePayload;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String getIntendedChannelName() {
        return intendedChannelName;
    }

}
