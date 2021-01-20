package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public abstract class ChatChannel {

    private String name;
    private String prefix;
    private boolean crossServer;

    public ChatChannel(String name, String prefix, boolean crossServer) {
        this.name = name;
        this.prefix = prefix;
        this.crossServer = crossServer;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isCrossServer() {
        return crossServer;
    }

    public String format(ChatMessage chatMessage) {
        return getPrefix() + " " + chatMessage.getSender().getMinecraftUsername() + " > " + chatMessage.getContent();
    }

    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        return Bukkit.getOnlinePlayers();
    }

}
