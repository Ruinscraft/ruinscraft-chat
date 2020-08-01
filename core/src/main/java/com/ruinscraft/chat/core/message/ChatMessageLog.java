package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.ChatChannelType;
import com.ruinscraft.chat.api.IChatMessageLog;

import java.util.UUID;

public class ChatMessageLog implements IChatMessageLog {

    private String senderName;
    private String channelName;
    private ChatChannelType channelType;
    private String message;
    private String formattedMessage;
    private long timeMillis;
    private boolean blocked;

    public ChatMessageLog(String senderName, String channelName, ChatChannelType channelType, String message, String formattedMessage, long timeMillis, boolean blocked) {
        this.senderName = senderName;
        this.channelName = channelName;
        this.channelType = channelType;
        this.message = message;
        this.formattedMessage = formattedMessage;
        this.timeMillis = timeMillis;
        this.blocked = blocked;
    }

    @Override
    public String getSenderName() {
        return senderName;
    }

    @Override
    public UUID getSenderId() {
        return null;
    }

    @Override
    public String getChannelName() {
        return channelName;
    }

    @Override
    public ChatChannelType getChannelType() {
        return channelType;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getFormattedMessage() {
        return formattedMessage;
    }

    @Override
    public long getTimeMillis() {
        return timeMillis;
    }

    @Override
    public boolean wasBlocked() {
        return blocked;
    }

}
