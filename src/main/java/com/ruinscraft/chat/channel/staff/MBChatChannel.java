package com.ruinscraft.chat.channel.staff;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.BasicChatChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;

import java.util.StringJoiner;

public class MBChatChannel extends ChatChannel {

    private ChatPlugin chatPlugin;

    public MBChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "default", "mb", ChatColor.GRAY + "[" + ChatColor.GREEN + "mb" + ChatColor.GRAY + "]", ChatColor.GREEN, true);
        this.chatPlugin = chatPlugin;
    }

    public MBChatChannel(ChatPlugin chatPlugin, String name, String prefix, ChatColor chatColor) {
        super(chatPlugin, "default", name, prefix, chatColor, true);
        this.chatPlugin = chatPlugin;
    }

    @Override
    public String format(BasicChatChatMessage basicChatMessage) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        stringJoiner.add(getPrefix() + ChatColor.RESET);

        if (basicChatMessage.getSender() instanceof OnlineChatPlayer) {
            OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) basicChatMessage.getSender();
            stringJoiner.add(ChatColor.GRAY + "[" + getChatColor() + onlineChatPlayer.getServerName() + ChatColor.GRAY + "]" + ChatColor.RESET);
            stringJoiner.add(ChatColor.GRAY + "[" + getChatColor() + onlineChatPlayer.getGroupName() + ChatColor.GRAY + "]" + ChatColor.RESET);
        }

        stringJoiner.add(ChatColor.RED + basicChatMessage.getSender().getMinecraftUsername());
        stringJoiner.add(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ">");
        stringJoiner.add(getChatColor() + basicChatMessage.getContent());

        return stringJoiner.toString();
    }

}
