package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MailMessage extends Message {

    private ChatPlayer recipient;
    private boolean read;

    public MailMessage(UUID id, long time, ChatPlayer sender, String content, ChatPlayer recipient, boolean read) {
        super(id, time, sender, content);
        this.recipient = recipient;
        this.read = read;
    }

    public MailMessage(ChatPlayer sender, String content, ChatPlayer recipient) {
        this(UUID.randomUUID(), System.currentTimeMillis(), sender, content, recipient, false);
    }

    public ChatPlayer getRecipient() {
        return recipient;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    @Override
    protected void show0(ChatPlugin chatPlugin, Player to) {
        to.sendMessage(ChatColor.GOLD + "Message from: " + getSender().getMinecraftUsername());
        to.sendMessage(ChatColor.GOLD + "Received " + getDurationSinceSentWords() + " ago.");
        to.sendMessage("  >> " + ChatColor.GRAY + getContent());
    }

    @Override
    protected void showChatSpy(ChatPlugin chatPlugin, Player staff) {
        // Do nothing?
    }

    @Override
    protected void showConsole(ChatPlugin chatPlugin) {
        // Do nothing?
    }

}
