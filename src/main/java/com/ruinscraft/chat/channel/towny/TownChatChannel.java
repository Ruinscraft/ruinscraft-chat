package com.ruinscraft.chat.channel.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class TownChatChannel extends TownyChatChannel {

    public TownChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "townchat", ChatColor.AQUA + "[T]", ChatColor.YELLOW, false);
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        Resident resident;
        Town town;

        try {
            resident = TownyAPI.getInstance().getDataSource().getResident(player.getName());
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "No one heard you because you are not in a town. Switch back to global chat with /g");
            return new HashSet<>();
        }

        return TownyAPI.getInstance().getOnlinePlayers(town);
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("tc");
        return command;
    }

}
