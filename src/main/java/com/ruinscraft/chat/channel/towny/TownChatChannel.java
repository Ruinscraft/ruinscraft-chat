package com.ruinscraft.chat.channel.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class TownChatChannel extends TownyChatChannel {

    public TownChatChannel() {
        super("town", "[T]", false);
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        Resident resident;
        Town town;

        try {
            resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
            town = resident.getTown();
        } catch (NotRegisteredException e) {
            return new HashSet<>();
        }

        return TownyAPI.getInstance().getOnlinePlayers(town);
    }

}
