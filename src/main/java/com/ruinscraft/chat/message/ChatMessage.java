package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.GlobalChatChannel;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.StringJoiner;
import java.util.UUID;

public class ChatMessage extends Message {

    private UUID originServerId;
    private String channelDbName;

    public ChatMessage(UUID id, long time, ChatPlayer sender, String content, UUID originServerId, String channelDbName) {
        super(id, time, sender, content);
        this.originServerId = originServerId;
        this.channelDbName = channelDbName;
    }

    public ChatMessage(ChatPlayer sender, String content, UUID originServerId, String channelDbName) {
        this(UUID.randomUUID(), System.currentTimeMillis(), sender, content, originServerId, channelDbName);
    }

    public UUID getOriginServerId() {
        return originServerId;
    }

    public String getChannelDbName() {
        return channelDbName;
    }

    @Override
    protected void show0(ChatPlugin chatPlugin, Player to) {
        ChatChannel chatChannel = chatPlugin.getChatChannelManager().getChannel(channelDbName);

        if (chatChannel.getPermission() != null) {
            if (!to.hasPermission(chatChannel.getPermission())) {
                return;
            }
        }

        if (!chatChannel.isCrossServer()) {
            if (!originServerId.equals(chatPlugin.getServerId())) {
                return;
            }
        }

        ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(getChannelDbName());
        String message = channel.format(this);

        to.sendMessage(message);
    }

    @Override
    protected void showChatSpy(ChatPlugin chatPlugin, Player staff) {
        if (staff.getUniqueId().equals(getSender().getMojangId())) {
            return;
        }

        ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(getChannelDbName());

        if (channel == null) {
            return;
        }

        if (channel.getRecipients(this).contains(staff)) {
            if (channel instanceof GlobalChatChannel) {
                Player senderPlayer = Bukkit.getPlayer(getSender().getMojangId());

                if (senderPlayer != null && senderPlayer.isOnline()) {
                    return;
                }
            } else {
                return;
            }
        }

        StringJoiner stringJoiner = new StringJoiner(" ");

        if (getSender() instanceof OnlineChatPlayer) {
            OnlineChatPlayer onlineSender = (OnlineChatPlayer) getSender();

            stringJoiner.add(ChatColor.GRAY + "[" + onlineSender.getServerName() + "] " + onlineSender.getMinecraftUsername() + ": " + getContent());
        }

        staff.sendMessage(stringJoiner.toString());
    }

    @Override
    protected void showConsole(ChatPlugin chatPlugin) {
        ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(getChannelDbName());

        if (channel == null) {
            return;
        }

        if (channel.isCrossServer()) {
            if (!chatPlugin.getServerId().equals(originServerId)) {
                return;
            }
        }

        System.out.println(channel.format(this));
    }

}
