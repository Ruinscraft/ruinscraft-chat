package com.ruinscraft.chat.core.message;

import com.ruinscraft.chat.api.IChatMessage;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.core.player.ChatPlayer;

public class ChatMessage implements IChatMessage {

    private ChatPlayer sender;
    private String content;

    public ChatMessage(ChatPlayer sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    @Override
    public IChatPlayer getSender() {
        return sender;
    }

    @Override
    public String getContent() {
        return content;
    }

}
