package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatMessage;

public abstract class ChatChannel implements IChatChannel {

    private String name;
    private String description;
    private String permission;
    private boolean mutable;
    private boolean spyable;
    private boolean filtered;

    public ChatChannel(String name, String description, String permission, boolean mutable, boolean spyable, boolean filtered) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.mutable = mutable;
        this.spyable = spyable;
        this.filtered = filtered;
    }

    public ChatChannel(String name, String description) {
        this(name, description, null, true, true, true);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void send(IChatMessage message) {

    }

}
