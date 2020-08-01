package com.ruinscraft.chat.api;

import java.util.UUID;

public interface IChatMessageLog {

    String getSenderName();

    UUID getSenderId();

    String getChannelName();

    ChatChannelType getChannelType();

    String getMessage();

    String getFormattedMessage();

    long getTimeMillis();

    boolean wasBlocked();

}
