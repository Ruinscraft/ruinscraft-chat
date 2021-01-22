package com.ruinscraft.chat.message;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.ChatPlayer;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.FilterUtil;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public abstract class Message {

    private UUID id;
    private long time;
    private ChatPlayer sender;
    private String content;

    public Message(UUID id, long time, ChatPlayer sender, String content) {
        this.id = id;
        this.time = time;
        this.sender = sender;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public ChatPlayer getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getDurationSinceSentWords() {
        long duration = System.currentTimeMillis() - time;
        return DurationFormatUtils.formatDurationWords(duration, true, true);
    }

    @Override
    public boolean equals(Object o) {
        Message that = (Message) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public void show(ChatPlugin chatPlugin, Player to) {
        OnlineChatPlayer toChatPlayer = chatPlugin.getChatPlayerManager().get(to);

        if (!chatPlugin.getSpamHandler().canSendMessage(getSender())) {
            return;
        }

        if (toChatPlayer.isBlocked(sender)) {
            return;
        }

        if (toChatPlayer.getPersonalizationSettings().isHideProfanity()) {
            if (FilterUtil.isBadMessage(chatPlugin.getBadWords(), content)) {
                return;
            }
        }

        show0(chatPlugin, to);
    }

    protected abstract void show0(ChatPlugin chatPlugin, Player to);

}
