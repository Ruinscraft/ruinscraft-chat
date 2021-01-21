package com.ruinscraft.chat.channel.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class NationChatChannel extends TownyChatChannel {

    public NationChatChannel(ChatPlugin chatPlugin) {
        super(chatPlugin, "nationchat", ChatColor.AQUA + "[N]", ChatColor.GOLD, false);
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        Resident resident;
        Nation nation;

        try {
            resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
            nation = resident.getTown().getNation();
        } catch (NotRegisteredException e) {
            player.sendMessage(ChatColor.RED + "No one heard you because you are not in a nation. Switch back to global chat with /g");
            return new HashSet<>();
        }

        return TownyAPI.getInstance().getOnlinePlayers(nation);
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("nc");
        return command;
    }

}
