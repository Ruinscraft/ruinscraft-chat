package com.ruinscraft.chat;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.players.ChatPlayer;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatUtil {

	public static Collection<Player> getOnlinePlayersNotIgnored(Player player) {
		Set<Player> players = new HashSet<>();
		
		ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

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
	
    public static BaseComponent[] convertFromLegacy(final String legacyText) {
        return TextComponent.fromLegacyText(legacyText);
    }
    
    public static int getInventorySlotCount(int itemsDesired) {
    	return (itemsDesired + 8) / 9 * 9;
    }
	
}
