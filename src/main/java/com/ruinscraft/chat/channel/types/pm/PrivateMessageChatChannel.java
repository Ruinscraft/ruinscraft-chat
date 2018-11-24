package com.ruinscraft.chat.channel.types.pm;

import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.ruinscraft.playerstatus.PlayerStatus;
import com.ruinscraft.playerstatus.PlayerStatusPlugin;

public class PrivateMessageChatChannel implements ChatChannel<PrivateChatMessage> {

	private ReplyStorage replyStorage;

	public PrivateMessageChatChannel(ConfigurationSection replyStorageSection) {
		if (replyStorageSection.getBoolean("redis.use")) {
			replyStorage = new RedisReplyStorage(replyStorageSection.getConfigurationSection("redis"));
		}
	}

	public ReplyStorage getReplyCache() {
		return replyStorage;
	}

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
	public boolean isLogged() {
		return true;
	}

	@Override
	public void send(PrivateChatMessage chatMessage) {
		Player sender = Bukkit.getPlayer(chatMessage.getSender());

		Callable<PlayerStatus> callable = PlayerStatusPlugin.getAPI().getPlayerStatus(chatMessage.getRecipient());

		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try {
				PlayerStatus playerStatus = callable.call();

				if (sender == null || !sender.isOnline()) {
					return;
				}

				if (!playerStatus.isOnline()) {
					sender.sendMessage(ChatColor.RED + chatMessage.getRecipient() + " is not online.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

}
