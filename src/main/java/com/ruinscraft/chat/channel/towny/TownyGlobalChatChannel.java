package com.ruinscraft.chat.channel.towny;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.ruinscraft.chat.channel.GlobalChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TownyGlobalChatChannel extends GlobalChatChannel {

    @Override
    public String format(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        Resident resident;

        try {
            resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
        } catch (NotRegisteredException e) {
            return super.format(chatMessage);
        }

        if (resident.hasTown()) {
            Town town;

            try {
                town = resident.getTown();
            } catch (NotRegisteredException e) {
                return super.format(chatMessage);
            }

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ChatColor.GRAY + "[" + ChatColor.LIGHT_PURPLE + town.getName());

            if (resident.hasNation()) {
                Nation nation;

                try {
                    nation = town.getNation();
                } catch (NotRegisteredException e) {
                    return super.format(chatMessage);
                }

                stringBuilder.append(ChatColor.GRAY + ", " + ChatColor.DARK_PURPLE + nation.getName());
            }

            stringBuilder.append(ChatColor.GRAY + "]" + ChatColor.RESET);
            stringBuilder.append(" ");
            stringBuilder.append(super.format(chatMessage));

            return stringBuilder.toString();
        }

        return super.format(chatMessage);
    }

}
