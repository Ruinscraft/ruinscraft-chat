package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.StringJoiner;
import java.util.UUID;

public class DirectMessage extends ChatMessage {

    private ChatPlayer recipient;

    public DirectMessage(UUID id, long time, ChatPlayer sender, String content, UUID originServerId, String channelDbName, ChatPlayer recipient) {
        super(id, time, sender, content, originServerId, channelDbName);
        this.recipient = recipient;
    }

    public DirectMessage(ChatPlayer sender, String content, UUID originServerId, ChatPlayer recipient) {
        this(UUID.randomUUID(), System.currentTimeMillis(), sender, content, originServerId, "dm:" + recipient.getMojangId(), recipient);
    }

    public ChatPlayer getRecipient() {
        return recipient;
    }

    @Override
    protected void show0(ChatPlugin chatPlugin, Player to) {
        // If "to" is sender or recipient...

        // Is sender
        if (getSender().getMojangId().equals(to.getUniqueId())) {
            if (getSender() instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineSender = (OnlineChatPlayer) getSender();
                onlineSender.setLastDm(recipient.getMojangId());
            }

            ComponentBuilder componentBuilder = new ComponentBuilder("[to: " + recipient.getMinecraftUsername() + "] ")
                    .color(ChatColor.DARK_AQUA)
                    .append(getContent())
                    .color(ChatColor.AQUA);

            to.spigot().sendMessage(componentBuilder.create());
        }

        // Is recipient
        if (recipient.getMojangId().equals(to.getUniqueId())) {
            if (recipient instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineRecipient = (OnlineChatPlayer) recipient;
                onlineRecipient.setLastDm(getSender().getMojangId());
            }

            showNewMessageActionBar(to, getSender().getMinecraftUsername());

            ComponentBuilder componentBuilder = new ComponentBuilder("[from: " + getSender().getMinecraftUsername() + "] ")
                    .color(ChatColor.DARK_AQUA);

            if (getSender() instanceof OnlineChatPlayer) {
                OnlineChatPlayer onlineSender = (OnlineChatPlayer) getSender();
                componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        TextComponent.fromLegacyText(onlineSender.getMinecraftUsername() + " is on " + onlineSender.getServerName())));
            }

            componentBuilder.append(getContent()).color(ChatColor.AQUA);
            to.spigot().sendMessage(componentBuilder.create());
        }
    }

    @Override
    protected void showChatSpy(ChatPlugin chatPlugin, Player staff) {
        if (staff.getUniqueId().equals(getSender().getMojangId())) {
            return;
        }

        if (staff.getUniqueId().equals(getRecipient().getMojangId())) {
            return;
        }

        StringJoiner stringJoiner = new StringJoiner(" ");

        stringJoiner.add(org.bukkit.ChatColor.GRAY + "[" + getSender().getMinecraftUsername() +
                " -> " + getRecipient().getMinecraftUsername() + "] " + getContent());

        staff.sendMessage(stringJoiner.toString());
    }

    private void showNewMessageActionBar(Player player, String from) {
        TextComponent message = new TextComponent("New message from " + from);
        message.setColor(ChatColor.AQUA);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

}
