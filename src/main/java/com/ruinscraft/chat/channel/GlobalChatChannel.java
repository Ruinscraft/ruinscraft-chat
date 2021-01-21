package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.VaultUtil;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class GlobalChatChannel extends ChatChannel {

    public GlobalChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "default", "global", "", ChatColor.RESET, false);
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("g");
        return command;
    }

    @Override
    public String format(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());

        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(ChatColor.GRAY + "[" + VaultUtil.getPrefix(player) + ChatColor.GRAY + "]");
        stringJoiner.add(ChatColor.GRAY + player.getName());
        stringJoiner.add(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ">");
        stringJoiner.add(ChatColor.RESET + chatMessage.getContent());

        return stringJoiner.toString();
    }

}
