package com.ruinscraft.chat.channel.cinema;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.cinemadisplays.CinemaDisplaysPlugin;
import com.ruinscraft.cinemadisplays.theater.StaticTheater;
import com.ruinscraft.cinemadisplays.theater.Theater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;

public class TheaterChatChannel extends ChatChannel {

    private CinemaDisplaysPlugin cinemaDisplaysPlugin;

    public TheaterChatChannel(ChatPlugin chatPlugin, CinemaDisplaysPlugin cinemaDisplaysPlugin) {
        super(chatPlugin, "cinemadisplays", "theater", ChatColor.AQUA + "[T]", ChatColor.YELLOW, false);
        this.cinemaDisplaysPlugin = cinemaDisplaysPlugin;
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());

        if (player == null || !player.isOnline()) {
            return new HashSet<>();
        }

        Theater theater = cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(player);

        if (theater == null || theater instanceof StaticTheater) {
            player.sendMessage(ChatColor.RED + "Not in a theater. Switch to global chat with: /global");
            return new HashSet<>();
        }

        return theater.getViewers();
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("local"); // legacy command support
        return command;
    }

}
