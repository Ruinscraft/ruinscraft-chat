package com.ruinscraft.chat.core.channel;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatChannelManager {

    private ChatChannel _default;
    private Map<String, ChatChannel> cache;

    public ChatChannelManager(ChatChannel _default, Set<ChatChannel> channels) {
        this._default = _default;
        cache = new ConcurrentHashMap<>();
        channels.forEach(channel -> cache.put(channel.getName(), channel));
    }

    public ChatChannel get(String name) {
        return cache.get(name);
    }

    public ChatChannel getDefault() {
        return _default;
    }

    public void register(ChatChannel channel) {
        cache.put(channel.getName(), channel);
    }

    public void unregister(ChatChannel channel) {
        cache.remove(channel.getName());
    }

}
