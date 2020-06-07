package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;

public class ChatMessage implements IChatMessage {

    private IChatPlayer sender;
    private String content;
    private long time;

    private ChatMessage(IChatPlayer sender, String content, long time) {
        this.sender = sender;
        this.content = content;
        this.time = time;
    }

    @Override
    public IChatPlayer getSender() {
        return sender;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public long getTime() {
        return time;
    }

    public static ChatMessage of(IChatPlayer sender, String content) {
        return new ChatMessage(sender, content, System.currentTimeMillis());
    }

}
