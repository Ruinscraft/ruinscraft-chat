package com.ruinscraft.chat.channel.types;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.message.GenericChatMessage;

public class PlotLocalChatChannel extends DefaultLocalChatChannel {

	@Override
	public String getPrettyName() {
		return "Local Chat (the plot you are on)";
	}

	@Override
	public Collection<? extends Player> getIntendedRecipients(GenericChatMessage context) {
		Player player = Bukkit.getPlayer(context.getSender());

		if (player == null || !player.isOnline()) {
			return new HashSet<>();
		}
		
		PlotPlayer plotPlayer = PlotPlayer.wrap(player);
		
		if (plotPlayer == null || !plotPlayer.isOnline()) {
			return new HashSet<>();
		}
		
		Plot currentPlot = plotPlayer.getCurrentPlot();
		
		if (currentPlot == null) {
			player.sendMessage(Constants.COLOR_BASE + "You must be in a plot for this local chat channel");
			return new HashSet<>();
		}
		
		Set<Player> recipients = new HashSet<>();
		
		for (Player recipient : super.getIntendedRecipients(context)) {
			if (currentPlot.getPlayersInPlot().contains(PlotPlayer.wrap(recipient))) {
				recipients.add(recipient);
			}
		}

		if (recipients.size() == 1 && recipients.contains(player)) {
			player.sendMessage(Constants.COLOR_BASE + "No one is in your plot to hear you");
		}
		
		return recipients;
	}
	
}
