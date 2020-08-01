package com.ruinscraft.chat.core.player;

import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatSettings;

import java.util.HashSet;
import java.util.Set;

public class ChatSettings implements IChatSettings {

    private Set<IChatChannel> disabled;

    public ChatSettings() {
        disabled = new HashSet<>();
    }

    @Override
    public Set<IChatChannel> getDisabled() {
        return disabled;
    }

}
