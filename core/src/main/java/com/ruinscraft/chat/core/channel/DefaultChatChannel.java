package com.ruinscraft.chat.core.channel;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.api.IMessageFormatter;
import com.ruinscraft.chat.core.ChatPlatform;
import com.ruinscraft.chat.core.message.DefaultMessageFormatter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultChatChannel extends ChatChannel {

    private ChatPlatform platform;
    private IMessageFormatter formatter;

    public DefaultChatChannel(ChatPlatform platform) {
        super("default");
        this.platform = platform;
        formatter = new DefaultMessageFormatter();
    }

    @Override
    public IMessageFormatter getFormatter() {
        return formatter;
    }

    @Override
    public Set<IChatPlayer> getRecipients() {
        IChat chat = platform.getChat();

        Set<UUID> online = platform.getOnlinePlayers();
        Set<IChatPlayer> recipients = new HashSet<>();

        online.forEach(uuid -> recipients.add(chat.getPlayer(uuid)));

        return recipients;
    }

}
