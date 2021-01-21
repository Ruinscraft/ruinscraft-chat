package com.ruinscraft.chat.message;

import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DirectChatChatMessage implements ChatMessage {

    private UUID id;
    private UUID originServerId;
    private ChatPlayer sender;
    private ChatPlayer recipient;
    private long time;
    private String content;

    public DirectChatChatMessage(UUID id, UUID originServerId, ChatPlayer sender, ChatPlayer recipient, long time, String content) {
        this.id = id;
        this.originServerId = originServerId;
        this.sender = sender;
        this.recipient = recipient;
        this.time = time;
        this.content = content;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public UUID getOriginServerId() {
        return originServerId;
    }

    @Override
    public String getChannelDbName() {
        return "dm:" + recipient.getMojangId().toString();
    }

    @Override
    public long getTime() {
        return time;
    }

    @Override
    public ChatPlayer getSender() {
        return sender;
    }

    @Override
    public String getContent() {
        return content;
    }

    public ChatPlayer getRecipient() {
        return recipient;
    }

    public void show() {
        Player senderPlayer = Bukkit.getPlayer(sender.getMojangId());
        Player recipientPlayer = Bukkit.getPlayer(recipient.getMojangId());

        if (senderPlayer != null && senderPlayer.isOnline()) {
            ComponentBuilder componentBuilder = new ComponentBuilder("[to: " + recipient.getMinecraftUsername() + "] ")
                    .color(ChatColor.DARK_AQUA)
                    .append(content)
                    .color(ChatColor.AQUA);

            senderPlayer.spigot().sendMessage(componentBuilder.create());
        }

        if (recipientPlayer != null && recipientPlayer.isOnline()) {
            showNewMessageActionBar(recipientPlayer, sender.getMinecraftUsername());

            ComponentBuilder componentBuilder = new ComponentBuilder("[from: " + sender.getMinecraftUsername() + "] ")
                    .color(ChatColor.DARK_AQUA);

            if (sender instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineSender = (OnlineChatPlayer) sender;

                componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(onlineSender.getMinecraftUsername() + " is on " + onlineSender.getServerName())));
            }

            componentBuilder.append(content).color(ChatColor.AQUA);

            senderPlayer.spigot().sendMessage(componentBuilder.create());
        }
    }

    private void showNewMessageActionBar(Player player, String from) {
        TextComponent message = new TextComponent("New message from " + from);
        message.setColor(ChatColor.AQUA);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

}
