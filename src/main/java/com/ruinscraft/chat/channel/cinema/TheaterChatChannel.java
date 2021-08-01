package com.ruinscraft.chat.channel.cinema;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.cinemadisplays.CinemaDisplaysPlugin;
import com.ruinscraft.cinemadisplays.theater.Theater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TheaterChatChannel extends ChatChannel {

    private CinemaDisplaysPlugin cinemaDisplaysPlugin;

    public TheaterChatChannel(ChatPlugin chatPlugin, CinemaDisplaysPlugin cinemaDisplaysPlugin) {
        super(chatPlugin, "cinemadisplays", "theater", "[T]", ChatColor.YELLOW, false);
        this.cinemaDisplaysPlugin = cinemaDisplaysPlugin;
    }

    public boolean isInTheater(Player player) {
        return cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(player) != null;
    }

    public Set<Player> getPlayersNotInTheater() {
        return cinemaDisplaysPlugin.getServer().getOnlinePlayers().stream()
                .filter(p -> cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(p) == null)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());

        if (player == null || !player.isOnline()) {
            return new HashSet<>();
        }

        if (isInTheater(player)) {
            Theater theater = cinemaDisplaysPlugin.getTheaterManager().getCurrentTheater(player);
            return theater.getViewers();
        } else {
            return getPlayersNotInTheater();
        }
    }

}
