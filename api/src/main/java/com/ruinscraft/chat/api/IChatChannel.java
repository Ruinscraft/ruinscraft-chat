package com.ruinscraft.chat.api;

import java.util.Collection;

public interface IChatChannel<MTYPE extends IChatMessage> {

    String getName();

    String getPermission();

    boolean isMutable();

    boolean isSpyable();

    boolean isFiltered();

    void send(MTYPE message);

    IMessageFormatter getFormatter();

    Collection<IChatPlayer> getRecipients();

}
