package com.ruinscraft.chat.api;

import java.util.Set;

public interface IChatChannel<CMTYPE extends IChatMessage> {

    String getName();

    String getPermission();

    boolean isMutable();

    boolean isSpyable();

    boolean isFiltered();

    void send(CMTYPE chatMessage);

    IMessageFormatter getFormatter();

    Set<IChatPlayer> getRecipients();

}
