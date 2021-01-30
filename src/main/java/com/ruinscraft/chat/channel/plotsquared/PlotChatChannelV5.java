package com.ruinscraft.chat.channel.plotsquared;

import com.plotsquared.core.player.PlotPlayer;
import com.plotsquared.core.plot.Plot;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PlotChatChannelV5 extends ChatChannel {

    public PlotChatChannelV5(ChatPlugin chatPlugin) {
        super(chatPlugin, "plotsquared", "plotchat", ChatColor.AQUA + "[P]", ChatColor.YELLOW, false);
    }

    @Override
    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        Set<Player> recipients = new HashSet<>();
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());

        if (player == null || !player.isOnline()) {
            return new HashSet<>();
        }

        PlotPlayer plotPlayer = PlotPlayer.wrap(player);

        if (plotPlayer == null) {
            return new HashSet<>();
        }

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

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("local"); // legacy command support
        return command;
    }

}
