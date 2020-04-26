package com.ruinscraft.chat.api;

public interface IChatChannel<MTYPE extends IChatMessage> {

    String getName();

    String getDescription();

    String getPermission();

    boolean isMutable();

    boolean isSpyable();

    boolean isFiltered();

    void send(MTYPE message);

    IMessageFormatter getFormatter();

}
