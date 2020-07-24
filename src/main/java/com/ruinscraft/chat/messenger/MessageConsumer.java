package com.ruinscraft.chat.messenger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.types.GlobalChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.message.PrivateChatMessage;

public interface MessageConsumer {

    Gson GSON = new Gson();

    default void consume(Message message) {
        Object payload = null;

        try {
            payload = GSON.fromJson(message.getPayload(), Class.forName(message.getPayloadClass()));
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (payload instanceof ChatMessage) {
            ChatMessage chatMessage = (ChatMessage) payload;

            if (chatMessage instanceof PrivateChatMessage) {
                PrivateChatMessage privateChatMessage = (PrivateChatMessage) chatMessage;
                ChatChannel<PrivateChatMessage> intendedChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(chatMessage.getIntendedChannelName());
                intendedChannel.sendToChat(privateChatMessage);
            }

            else if (chatMessage instanceof GenericChatMessage) {
                GenericChatMessage genericChatMessage = (GenericChatMessage) chatMessage;
                ChatChannel<GenericChatMessage> intendedChannel = ChatPlugin.getInstance().getChatChannelManager().getByName(chatMessage.getIntendedChannelName());
                if (ChatPlugin.getInstance().getConfig().getBoolean("channels.disable") && intendedChannel instanceof GlobalChatChannel) {
                    return;
                }

                // if global disabled and channel is global
                if (!ChatPlugin.getInstance().getConfig().getBoolean("channels.enable-global") && intendedChannel instanceof GlobalChatChannel) {
                    return;
                }

                intendedChannel.sendToChat(genericChatMessage);
            }
        }
    }

}