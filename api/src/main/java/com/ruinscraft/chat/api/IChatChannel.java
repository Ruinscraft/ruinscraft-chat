package com.ruinscraft.chat.api;

import java.util.Set;

public interface IChatChannel<MTYPE extends IChatMessage> {

    String getName();

    String getPermission();

    boolean isMutable();

    boolean isSpyable();

    boolean isFiltered();

    void send(MTYPE message);

    IMessageFormatter getFormatter();

    Set<IChatPlayer> getRecipients();

}
