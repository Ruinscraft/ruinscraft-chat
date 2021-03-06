package com.ruinscraft.chat.util;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.event.ChatPlayerLoginEvent;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.UUID;

public final class NetworkUtil {

    private static BungeeMessageListener M_LISTENER_BUNGEE;

    public static void register(ChatPlugin chatPlugin) {
        M_LISTENER_BUNGEE = new BungeeMessageListener(chatPlugin);

        chatPlugin.getServer().getMessenger().registerOutgoingPluginChannel(chatPlugin, "BungeeCord");
        chatPlugin.getServer().getMessenger().registerIncomingPluginChannel(chatPlugin, "BungeeCord", M_LISTENER_BUNGEE);
    }

    public static void unregister(ChatPlugin chatPlugin) {
        chatPlugin.getServer().getMessenger().unregisterIncomingPluginChannel(chatPlugin, "BungeeCord");
        chatPlugin.getServer().getMessenger().unregisterOutgoingPluginChannel(chatPlugin, "BungeeCord");
    }

    public static void sendChatEventPacket(ChatPlugin chatPlugin, Player player, UUID chatMessageId) {
        {
            ChatUtil.handleChatMessage(chatPlugin, chatMessageId);
        }
        {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("chat_event");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(chatMessageId.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            byte[] data = out.toByteArray();
            player.sendPluginMessage(chatPlugin, "BungeeCord", data);
        }
    }

    public static void sendPrivateChatEventPacket(ChatPlugin chatPlugin, Player player, UUID privateChatMessageId) {
        {
            ChatUtil.handleChatMessage(chatPlugin, privateChatMessageId);
        }
        {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("private_chat_event");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(privateChatMessageId.toString());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            byte[] data = out.toByteArray();
            player.sendPluginMessage(chatPlugin, "BungeeCord", data);
        }
    }

    private static void sendChatPlayerLoginEvent(ChatPlugin chatPlugin, OnlineChatPlayer onlineChatPlayer) {
        chatPlugin.getServer().getScheduler().runTask(chatPlugin, () -> {
            ChatPlayerLoginEvent event = new ChatPlayerLoginEvent(onlineChatPlayer);
            chatPlugin.getServer().getPluginManager().callEvent(event);
        });
    }

    public static void sendChatPlayerLoginPacket(ChatPlugin chatPlugin, OnlineChatPlayer onlineChatPlayer, Player player) {
        {
            sendChatPlayerLoginEvent(chatPlugin, onlineChatPlayer);
        }
        {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Forward");
            out.writeUTF("ALL");
            out.writeUTF("chat_player_login");
            ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
            DataOutputStream msgout = new DataOutputStream(msgbytes);
            try {
                msgout.writeUTF(player.getUniqueId().toString());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            out.writeShort(msgbytes.toByteArray().length);
            out.write(msgbytes.toByteArray());
            byte[] data = out.toByteArray();
            chatPlugin.getServer().getScheduler().runTaskLater(chatPlugin, () -> {
                player.sendPluginMessage(chatPlugin, "BungeeCord", data);
            }, 20L);
        }
    }

    public static void sendServerNameRequestBungeePacket(Player player, JavaPlugin javaPlugin) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(javaPlugin, "BungeeCord", out.toByteArray());
    }

    private static class BungeeMessageListener implements PluginMessageListener {
        private ChatPlugin chatPlugin;

        public BungeeMessageListener(ChatPlugin chatPlugin) {
            this.chatPlugin = chatPlugin;
        }

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
            } else if (method.equals("chat_event")) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                UUID chatMessageId;
                try {
                    chatMessageId = UUID.fromString(msgin.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ChatUtil.handleChatMessage(chatPlugin, chatMessageId);
            } else if (method.equals("private_chat_event")) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                UUID chatMessageId;
                try {
                    chatMessageId = UUID.fromString(msgin.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                ChatUtil.handleChatMessage(chatPlugin, chatMessageId);
            } else if (method.equals("chat_player_login")) {
                short len = in.readShort();
                byte[] msgbytes = new byte[len];
                in.readFully(msgbytes);
                DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
                UUID playerId;
                try {
                    playerId = UUID.fromString(msgin.readUTF());
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                chatPlugin.getChatStorage().queryOnlineChatPlayer(playerId).thenAccept(onlineChatPlayerQuery -> {
                    if (onlineChatPlayerQuery.hasResults()) {
                        sendChatPlayerLoginEvent(chatPlugin, onlineChatPlayerQuery.getFirst());
                    }
                });
            }
        }
    }

}
