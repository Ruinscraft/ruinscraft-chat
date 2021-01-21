package com.ruinscraft.chat;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.BasicChatChatMessage;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.message.DirectChatChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public final class NetworkUtil {

    private static final String CHANNEL_CHAT = "ruinscraft:chat";
    private static ChatPluginMessageListener M_LISTENER_CHAT;
    private static BungeeMessageListener M_LISTENER_BUNGEE;

    public static void register(ChatPlugin chatPlugin) {
        M_LISTENER_CHAT = new ChatPluginMessageListener(chatPlugin);
        M_LISTENER_BUNGEE = new BungeeMessageListener();

        chatPlugin.getServer().getMessenger().registerOutgoingPluginChannel(chatPlugin, CHANNEL_CHAT);
        chatPlugin.getServer().getMessenger().registerIncomingPluginChannel(chatPlugin, CHANNEL_CHAT, M_LISTENER_CHAT);
        chatPlugin.getServer().getMessenger().registerOutgoingPluginChannel(chatPlugin, "BungeeCord");
        chatPlugin.getServer().getMessenger().registerIncomingPluginChannel(chatPlugin, "BungeeCord", M_LISTENER_BUNGEE);
    }

    public static void unregister(ChatPlugin chatPlugin) {
        chatPlugin.getServer().getMessenger().unregisterIncomingPluginChannel(chatPlugin, CHANNEL_CHAT);
        chatPlugin.getServer().getMessenger().unregisterOutgoingPluginChannel(chatPlugin, CHANNEL_CHAT);
    }

    public static void sendChatEventPacket(Player player, JavaPlugin javaPlugin, UUID chatMessageId) {
        String subChannel = "chat_event";
        String argument = chatMessageId.toString();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(argument);
        byte[] data = out.toByteArray();

        // First, send locally
        if (M_LISTENER_CHAT != null) {
            M_LISTENER_CHAT.onPluginMessageReceived(subChannel, player, data);
        }

        // Then, send out
        player.sendPluginMessage(javaPlugin, CHANNEL_CHAT, data);
    }

    public static void sendPrivateChatEventPacket(Player player, JavaPlugin javaPlugin, UUID privateChatMessageId) {
        String subChannel = "private_chat_event";
        String argument = privateChatMessageId.toString();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel);
        out.writeUTF(argument);
        byte[] data = out.toByteArray();

        // First, send locally
        if (M_LISTENER_CHAT != null) {
            M_LISTENER_CHAT.onPluginMessageReceived(subChannel, player, data);
        }

        // Then, send out
        player.sendPluginMessage(javaPlugin, CHANNEL_CHAT, data);
    }

    public static void sendServerNameRequestBungeePacket(Player player, JavaPlugin javaPlugin) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(javaPlugin, "BungeeCord", out.toByteArray());
    }

    private static class BungeeMessageListener implements PluginMessageListener {
        @Override
        public void onPluginMessageReceived(String channel, Player player, byte[] data) {
            if (!channel.equals("BungeeCord")) {
                return;
            }

            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            String method = in.readUTF();

            if (method.equals("GetServer")) {
                String name = in.readUTF();
                ChatPlugin.serverName = name;
            }
        }
    }

    private static class ChatPluginMessageListener implements PluginMessageListener {
        private ChatPlugin chatPlugin;

        public ChatPluginMessageListener(ChatPlugin chatPlugin) {
            this.chatPlugin = chatPlugin;
        }

        @Override
        public void onPluginMessageReceived(String subChannel, Player player, byte[] data) {
            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            String method = in.readUTF();

            if (method.equals("chat_event")) {
                UUID chatMessageId = UUID.fromString(in.readUTF());

                chatPlugin.getChatStorage().queryChatMessage(chatMessageId).thenAccept(chatMessageQuery -> {
                    if (chatMessageQuery.hasResults()) {
                        ChatMessage chatMessage = chatMessageQuery.getFirst();
                        ChatChannel chatChannel = chatPlugin.getChatChannelManager().getChannel(chatMessage.getChannelDbName());

                        if (!chatChannel.isCrossServer()) {
                            if (!chatMessage.getOriginServerId().equals(chatPlugin.getServerId())) {
                                return;
                            }
                        }

                        if (chatMessage instanceof BasicChatChatMessage) {
                            BasicChatChatMessage basicChatChatMessage = (BasicChatChatMessage) chatMessage;
                            basicChatChatMessage.showToChat(chatPlugin);
                        }
                    }
                });
            } else if (method.equals("private_chat_event")) {
                UUID privateChatMessageId = UUID.fromString(in.readUTF());

                chatPlugin.getChatStorage().queryChatMessage(privateChatMessageId).thenAccept(chatMessageQuery -> {
                    if (chatMessageQuery.hasResults()) {
                        ChatMessage chatMessage = chatMessageQuery.getFirst();

                        if (chatMessage instanceof DirectChatChatMessage) {
                            DirectChatChatMessage directChatChatMessage = (DirectChatChatMessage) chatMessage;
                            directChatChatMessage.show();
                        }
                    }
                });
            }
        }
    }

}
