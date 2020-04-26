package com.ruinscraft.chat.core;

import com.ruinscraft.chat.api.IChat;
import com.ruinscraft.chat.api.IChatChannel;
import com.ruinscraft.chat.api.IChatStorage;
import com.ruinscraft.chat.core.storage.MySQLChatStorage;

import java.util.HashMap;
import java.util.Map;

public class Chat implements IChat {

    private ChatConfig config;
    private IChatStorage storage;
    private Map<String, IChatChannel> registeredChannels;

    public ChatConfig getConfig() {
        if (config == null) {
            config = new ChatConfig();
        }

        return config;
    }

    @Override
    public IChatStorage getStorage() {
        return storage;
    }

    @Override
    public void registerChannel(IChatChannel chatChannel) {
        registeredChannels.put(chatChannel.getName(), chatChannel);
    }

    @Override
    public Map<String, IChatChannel> getRegisteredChannels() {
        return registeredChannels;
    }

    @Override
    public void start() {
        if (config.storageType.equals("mysql")) {
            storage = new MySQLChatStorage(true,
                    config.storageMySQLHost,
                    config.storageMySQLPort,
                    config.storageMySQLDatabase,
                    config.storageMySQLUsername,
                    config.storageMySQLPassword);
        }

        if (storage == null) {
            throw new RuntimeException("No storage defined");
        }

        registeredChannels = new HashMap<>();



    }

    @Override
    public void shutdown() {
        if (storage != null) {
            storage.close();
        }
    }

}
