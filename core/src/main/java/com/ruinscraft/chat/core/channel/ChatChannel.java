package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.messagebroker.IMessage;
import com.ruinscraft.chat.api.messagebroker.IMessageBroker;
import com.ruinscraft.chat.core.messagebroker.JsonMessage;

public abstract class ChatChannel<CMTYPE extends IChatMessage> implements IChatChannel<CMTYPE> {

    private String name;
    private String permission;
    private boolean mutable;
    private boolean spyable;
    private boolean filtered;

    private IChat chat;

    public ChatChannel(String name, String permission, boolean mutable, boolean spyable, boolean filtered, IChat chat) {
        this.name = name;
        this.permission = permission;
        this.mutable = mutable;
        this.spyable = spyable;
        this.filtered = filtered;
        this.chat = chat;
    }

    public ChatChannel(String name, IChat chat) {
        this(name, null, true, true, true, chat);
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isSpyable() {
        return spyable;
    }

    public boolean isFiltered() {
        return filtered;
    }

    public void send(CMTYPE chatMessage) {
        IMessageBroker messageBroker = chat.getMessageBroker();
        IMessage message = JsonMessage.createFromChatMessage(chatMessage); // TODO: this is dumb

        messageBroker.publish(message);
    }

}
