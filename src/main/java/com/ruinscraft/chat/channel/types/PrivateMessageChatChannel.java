package com.ruinscraft.chat.channel.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.ruinscraft.playerstatus.PlayerStatus;
import com.ruinscraft.playerstatus.PlayerStatusPlugin;

public class PrivateMessageChatChannel implements ChatChannel<PrivateChatMessage> {

	@Override
	public String getName() {
		return "pm";
	}

	@Override
	public String getFormat(PrivateChatMessage context) {
		return null;
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.AQUA;
	}

	@Override
	public String getPermission() {
		return null;
	}

	@Override
	public String[] getCommands() {
		// TODO: add more
		return new String[] {"message", "msg", "tell", "whisper", "w", "t", "m"};
	}

	@Override
	public boolean isOneToOne() {
		return true;
	}

	@Override
	public boolean isLogged() {
		return true;
	}

	@Override
	public void send(PrivateChatMessage chatMessage) {
		Player sender = Bukkit.getPlayer(chatMessage.getSender());

		if (sender == null || !sender.isOnline()) {
			return;
		}

		PlayerStatus playerStatus = null;

		try {
			playerStatus = PlayerStatusPlugin.getAPI().getPlayerStatus(chatMessage.getRecipient());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (playerStatus == null || !playerStatus.isOnline()) {
			sender.sendMessage(chatMessage.getRecipient() + " is not online.");
			return;
		}

		

	}

}
