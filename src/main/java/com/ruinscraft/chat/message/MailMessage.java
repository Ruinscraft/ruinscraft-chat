package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MailMessage implements Message {

    private UUID id;
    private UUID senderId;
    private UUID recipientId;
    private long time;
    private boolean read;
    private String content;

    public MailMessage(UUID id, UUID senderId, UUID recipientId, long time, boolean read, String content) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.time = time;
        this.read = read;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public UUID getRecipientId() {
        return recipientId;
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

    public CompletableFuture<Void> show(ChatPlugin chatPlugin, Player player) {
        return chatPlugin.getChatStorage().queryChatPlayer(senderId).thenAccept(chatPlayerQuery -> {
            if (chatPlayerQuery.hasResults()) {
                ChatPlayer chatPlayerSender = chatPlayerQuery.getFirst();
                String senderUsername = chatPlayerSender.getMinecraftUsername();

                if (player.isOnline()) {
                    player.sendMessage(ChatColor.GOLD + "========");
                    player.sendMessage(ChatColor.GOLD + "Message from: " + senderUsername);
                    player.sendMessage(content);
                    player.sendMessage(ChatColor.GOLD + "========");
                }
            }
        });
    }

}
