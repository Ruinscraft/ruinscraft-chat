package com.ruinscraft.chat.channel.types;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatMessage;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.playerstatus.PlayerStatus;
import com.ruinscraft.playerstatus.PlayerStatusPlugin;

public class PrivateMessageChatChannel implements ChatChannel {

	private String initiator;
	private String recipient;

	public PrivateMessageChatChannel(String initiator, String recipient) {
		this.initiator = initiator;
		this.recipient = recipient;
	}
	
	public String getInitiator() {
		return initiator;
	}
	
	public String getRecipient() {
		return recipient;
	}
	
	@Override
	public String getName() {
		return "pm";
	}

	@Override
	public String getFormat(String context) {
		if (context.equals(recipient)) {
			return getMessageColor() + "[from: %from%] %message";
		}
		return getMessageColor() + "[to: %to%] %message%";
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
	public void send(ChatMessage message) {
		Player player = Bukkit.getPlayer(message.getSender());
		
		if (player == null || !player.isOnline()) {
			return;
		}
		
		PlayerStatus playerStatus = null;
		
		try {
			playerStatus = PlayerStatusPlugin.getAPI().getPlayerStatus(recipient);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (playerStatus == null || !playerStatus.isOnline()) {
			player.sendMessage(recipient + " is not online.");
			return;
		}
		
		
		
	}
	
}
