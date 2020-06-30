package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.*;

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

    public void publish(CMTYPE chatMessage) {
        IMessageBroker messageBroker = chat.getMessageBroker();
        messageBroker.pushChatMessage(chatMessage, this);
    }

    @Override
    public void sendToChat(CMTYPE chatMessage) {
        for (IChatPlayer recipient : getRecipients()) {
            recipient.getServerPlayer().ifPresent(serverPlayer -> {
                // player has muted the channel
                if (recipient.isMuted(this)) {
                    return;
                }

                // message sender is blocked by player
                if (recipient.isBlocked(chatMessage.getSender())) {
                    return;
                }

                // no permission to view channel messages
                if (!serverPlayer.hasPermission(getPermission())) {
                    return;
                }

                serverPlayer.sendMessage(chatMessage, getFormatter());
            });
        }
    }

}
