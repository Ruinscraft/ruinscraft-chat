package com.ruinscraft.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.players.ChatPlayer;

public class ChatUtil {

	private static ChatPlugin chatPlugin = ChatPlugin.getInstance();
	
	public static Collection<Player> getOnlinePlayersNotIgnored(Player player) {
		Set<Player> players = new HashSet<>();
		
		ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getChatPlayer(player.getUniqueId());

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (chatPlayer.isIgnoring(onlinePlayer.getName())) {
				continue;
			}
			
			if (chatPlayer.isIgnoring(onlinePlayer.getUniqueId())) {
				continue;
			}
			
			players.add(onlinePlayer);
		}
		
		return players;
	}
	
}
