package com.ruinscraft.chat.channel.plotsquared;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PlotChatChannel extends ChatChannel {

    public PlotChatChannel() {
        super("plotsquared", "plot", "[P]", false);
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Set<Player> recipients = new HashSet<>();
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        PlotPlayer plotPlayer = PlotPlayer.wrap(player);
        Plot plot = plotPlayer.getCurrentPlot();

        if (plot == null) {
            player.sendMessage(ChatColor.RED + "You are not in a plot. No one can hear you.");
        } else {
            for (PlotPlayer inPlot : plot.getPlayersInPlot()) {
                Player inPlotPlayer = Bukkit.getPlayer(inPlot.getUUID());
                recipients.add(inPlotPlayer);
            }
        }

        return recipients;
    }

}
