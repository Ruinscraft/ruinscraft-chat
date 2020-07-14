package com.ruinscraft.chat.channel.types;

import com.ruinscraft.chat.message.GenericChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class HubLocalChatChannel extends DefaultLocalChatChannel {

    @Override
    public String getLabel(GenericChatMessage context) {
        return "";
    }

    @Override
    public Collection<? extends Player> getIntendedRecipients(GenericChatMessage context) {
        return Bukkit.getOnlinePlayers();
    }

}
