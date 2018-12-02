package com.ruinscraft.chat.channel.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.intellectualcrafters.plot.object.PlotPlayer;
import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class PlotLocalChatChannel extends DefaultLocalChatChannel {

	@Override
	public String getPrettyName() {
		return "Local Chat (the plot you are on)";
	}

	@Override
	public void sendToChat(GenericChatMessage chatMessage) {
		if (ChatPlugin.getInstance().getServerName() == null) {
			return;
		}

		if (!ChatPlugin.getInstance().getServerName().equals(chatMessage.getServerSentFrom())) {
			return;
		}

		Player sender = Bukkit.getPlayer(chatMessage.getSender());

		if (sender == null || !sender.isOnline()) {
			return;
		}

		PlotPlayer plotSender = PlotPlayer.wrap(sender);

		if (plotSender.getCurrentPlot() == null) {
			sender.sendMessage(Constants.COLOR_ERROR + "You must be in a plot for this local chat channel");
			return;
		}

		String message = chatMessage.getPayload();

		boolean someoneHeard = false;

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			PlotPlayer onlinePlotPlayer = PlotPlayer.wrap(onlinePlayer);

			if (!plotSender.getCurrentPlot().getPlayersInPlot().contains(onlinePlotPlayer)) {
				continue;
			}

			ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(onlinePlayer.getUniqueId());

			if (chatPlayer.isMuted(this)) {
				continue;
			}

			if (chatPlayer.isIgnoring(chatMessage.getSender())) {
				continue;
			}

			OfflinePlayer potentialOfflinePlayer = Bukkit.getOfflinePlayer(chatMessage.getSender());

			if (potentialOfflinePlayer != null && chatPlayer.isIgnoring(potentialOfflinePlayer.getUniqueId())) {
				continue;
			}

			// if no permission defined or they have it
			if (getPermission() == null || onlinePlayer.hasPermission(getPermission())) {
				String format = getFormat(onlinePlayer.getName(), chatMessage);

				format = format
						.replace("%prefix%", chatMessage.getSenderPrefix())
						.replace("%sender%", chatMessage.getSender());

				if (chatMessage.getSenderNickname() != null) {
					format = format.replace("%nickname%", chatMessage.getSenderNickname());
				}

				if (chatMessage.colorizePayload()) {
					format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', message));
				} else {
					format = format.replace("%message%", message);
				}

				onlinePlayer.sendMessage(format);

				if (!onlinePlayer.equals(sender)) {
					someoneHeard = true;
				}
			}
		}

		if (!someoneHeard) {
			sender.sendMessage(Constants.COLOR_ERROR + "No one is in your plot to hear you");
		}

		logAsync(chatMessage);
	}

}
