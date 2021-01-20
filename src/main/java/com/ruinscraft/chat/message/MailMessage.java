package com.ruinscraft.chat.message;

import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MailMessage implements Message {

    private UUID id;
    private ChatPlayer sender;
    private ChatPlayer recipient;
    private long time;
    private boolean read;
    private String content;

    public MailMessage(UUID id, ChatPlayer sender, ChatPlayer recipient, long time, boolean read, String content) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.time = time;
        this.read = read;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public ChatPlayer getSender() {
        return sender;
    }

    public ChatPlayer getRecipient() {
        return recipient;
    }

    public long getTime() {
        return time;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getContent() {
        return content;
    }

    public void show(Player player) {
        player.sendMessage(ChatColor.GOLD + "Message from: " + sender.getMinecraftUsername());
        player.sendMessage(ChatColor.GRAY + content);
    }

}
