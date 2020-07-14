package com.ruinscraft.chat.players;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;

import java.util.*;

public class ChatPlayer {

    private UUID mojangUUID;

    private int chatPlayerId;
    private ChatChannel<GenericChatMessage> focused;
    private String nickname;

    public Set<MinecraftIdentity> ignoring;
    public Set<ChatChannel<? extends ChatMessage>> muted;
    public Set<ChatChannel<? extends ChatMessage>> spying;
    public Map<String, String> meta;
    public transient Map<String, String> metaTransient;

    protected ChatPlayer(UUID mojangUUID) {
        this.mojangUUID = mojangUUID;
        this.ignoring = new HashSet<>();
        this.muted = new HashSet<>();
        this.spying = new HashSet<>();
        this.meta = new HashMap<>();
        this.metaTransient = new HashMap<>();
    }

    public UUID getMojangUUID() {
        return mojangUUID;
    }

    public void setChatPlayerId(int chatPlayerId) {
        this.chatPlayerId = chatPlayerId;
    }

    public int getChatPlayerId() {
        return chatPlayerId;
    }

    public void setFocused(ChatChannel<GenericChatMessage> chatChannel) {
        if (this.focused != null & (this.focused = chatChannel) != null) {
            save();
        }
    }

    public void setFocused(String channelName) {
        setFocused(ChatPlugin.getInstance().getChatChannelManager().getByName(channelName));
    }

    public ChatChannel<GenericChatMessage> getFocused() {
        if (focused instanceof GlobalChatChannel && !ChatPlugin.getInstance().getConfig().getBoolean("channels.enable-global")) {
            return ChatPlugin.getInstance().getChatChannelManager().getDefaultChatChannel();
        }

        return focused == null ? focused = ChatPlugin.getInstance().getChatChannelManager().getDefaultChatChannel() : focused;
    }

    public void setNickname(String nickname) {
        if (this.nickname != null & (this.nickname = nickname) != null) {
            save();
        }
    }

    public String getNickname() {
        return nickname;
    }

    public boolean hasNickname() {
        return nickname != null;
    }

    public boolean ignore(MinecraftIdentity minecraftIdentity) {
        boolean success = ignoring.add(minecraftIdentity);

        if (success) save();

        return success;
    }

    public boolean unignore(MinecraftIdentity minecraftIdentity) {
        boolean success = ignoring.remove(minecraftIdentity);

        if (success) save();

        return success;
    }

    public boolean isIgnoring(String username) {
        for (MinecraftIdentity minecraftIdentity : ignoring) {
            if (minecraftIdentity.getIdentity().equalsIgnoreCase(username)) {
                return true;
            }
        }

        return false;
    }

    public boolean isIgnoring(UUID uuid) {
        for (MinecraftIdentity minecraftIdentity : ignoring) {
            if (!minecraftIdentity.isUUID()) continue;

            if (minecraftIdentity.getIdentity().equalsIgnoreCase(uuid.toString())) {
                return true;
            }
        }

        return false;
    }

    public boolean mute(ChatChannel<? extends ChatMessage> chatChannel) {
        boolean success = muted.add(chatChannel);

        if (success) save();

        return success;
    }

    public boolean mute(String chatChannelName) {
        return mute(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public boolean unmute(ChatChannel<? extends ChatMessage> chatChannel) {
        boolean success = muted.remove(chatChannel);

        if (success) save();

        return success;
    }

    public boolean unmute(String chatChannelName) {
        return unmute(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public boolean isMuted(ChatChannel<? extends ChatMessage> chatChannel) {
        return muted.contains(chatChannel);
    }

    public boolean isMuted(String chatChannelName) {
        return isMuted(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public boolean spy(ChatChannel<? extends ChatMessage> chatChannel) {
        boolean success = spying.add(chatChannel);

        if (success) save();

        return success;
    }

    public boolean spy(String chatChannelName) {
        return spy(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public boolean unspy(ChatChannel<? extends ChatMessage> chatChannel) {
        boolean success = spying.remove(chatChannel);

        if (success) save();

        return success;
    }

    public boolean unspy(String chatChannelName) {
        return unspy(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public boolean isSpying(ChatChannel<? extends ChatMessage> chatChannel) {
        return spying.contains(chatChannel);
    }

    public boolean isSpying(String chatChannelName) {
        return isSpying(ChatPlugin.getInstance().getChatChannelManager().getByName(chatChannelName));
    }

    public void setMeta(String key, String value) {
        boolean changed = true;
        String previous = meta.get(key);

        if (previous != null && previous.equals(value)) {
            changed = false;
        }

        meta.put(key, value);

        if (changed) save();
    }

    public String getMeta(String key) {
        return meta.get(key);
    }

    public boolean hasMeta(String key) {
        return meta.get(key) != null;
    }

    public void setMetaTransient(String key, String value) {
        metaTransient.put(key, value);
    }

    public String getMetaTransient(String key) {
        return metaTransient.get(key);
    }

    public boolean hasMetaTransient(String key) {
        return metaTransient.get(key) != null;
    }

    public void save() {
        ChatPlugin.getInstance().getChatPlayerManager().save(this);
    }

}
