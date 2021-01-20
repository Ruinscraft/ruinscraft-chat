package com.ruinscraft.chat.channel.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class AllianceChatChannel extends TownyChatChannel {

    public AllianceChatChannel() {
        super("alliance", "[A]", false);
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
            return new HashSet<>();
        }

        return TownyAPI.getInstance().getOnlinePlayersAlliance(nation);
    }

}
