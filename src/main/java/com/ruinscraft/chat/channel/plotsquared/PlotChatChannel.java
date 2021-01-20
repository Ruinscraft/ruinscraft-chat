package com.ruinscraft.chat.channel.plotsquared;

import com.github.intellectualsites.plotsquared.plot.object.Plot;
import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.VaultUtil;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class PlotChatChannel extends ChatChannel {

    private ChatPlugin chatPlugin;

    public PlotChatChannel(ChatPlugin chatPlugin) {
        super("plotsquared", "plotchat", ChatColor.AQUA + "[L]", ChatColor.YELLOW, false);
        this.chatPlugin = chatPlugin;
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

    @Override
    public String format(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(getPrefix());
        stringJoiner.add(ChatColor.GRAY + "[" + VaultUtil.getPrefix(player) + ChatColor.GRAY + "]");
        stringJoiner.add(onlineChatPlayer.getPersonalizationSettings().getNameColor() + player.getName());
        stringJoiner.add(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ">");
        boolean hasNickname = !onlineChatPlayer.getPersonalizationSettings().getNickname().equals("");
        if (hasNickname) {
            stringJoiner.add(ChatColor.GOLD + "(" + onlineChatPlayer.getPersonalizationSettings().getNickname() + ")");
        }
        stringJoiner.add(getChatColor() + chatMessage.getContent());

        return stringJoiner.toString();
    }

    @Override
    public Command getCommand(ChatPlugin chatPlugin) {
        Command command = super.getCommand(chatPlugin);
        command.getAliases().add("local"); // legacy command support
        return command;
    }

}
