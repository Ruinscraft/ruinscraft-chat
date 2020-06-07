package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;

public abstract class ChatChannel<MTYPE extends IChatMessage> implements IChatChannel<MTYPE> {

    private String name;
    private String permission;
    private boolean mutable;
    private boolean spyable;
    private boolean filtered;

    public ChatChannel(String name, String permission, boolean mutable, boolean spyable, boolean filtered) {
        this.name = name;
        this.permission = permission;
        this.mutable = mutable;
        this.spyable = spyable;
        this.filtered = filtered;
    }

    public ChatChannel(String name) {
        this(name, null, true, true, true);
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

    public void send(MTYPE message) {
        for (IChatPlayer player : getRecipients()) {
            if (player.isMuted(this)) {
                continue;
            }

            if (player.isBlocked(message.getSender())) {
                continue;
            }

            player.sendMessage(message, getFormatter());
        }
    }

}
