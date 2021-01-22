package com.ruinscraft.chat.channel.staff;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;

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
    public String format(ChatMessage chatMessage) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        stringJoiner.add(getPrefix() + ChatColor.RESET);

        if (chatMessage.getSender() instanceof OnlineChatPlayer) {
            OnlineChatPlayer onlineChatPlayer = (OnlineChatPlayer) chatMessage.getSender();
            stringJoiner.add(ChatColor.GRAY + "[" + getChatColor() + onlineChatPlayer.getServerName() + ChatColor.GRAY + "]" + ChatColor.RESET);
            stringJoiner.add(ChatColor.GRAY + "[" + getChatColor() + onlineChatPlayer.getGroupName() + ChatColor.GRAY + "]" + ChatColor.RESET);
        }

        stringJoiner.add(ChatColor.RED + chatMessage.getSender().getMinecraftUsername());
        stringJoiner.add(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ">");
        stringJoiner.add(getChatColor() + chatMessage.getContent());

        return stringJoiner.toString();
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.setPermission("ruinscraft.chat." + getName());
        return command;
    }

}
