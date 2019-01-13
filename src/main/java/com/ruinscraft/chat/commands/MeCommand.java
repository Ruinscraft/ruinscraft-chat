package com.ruinscraft.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.types.DefaultLocalChatChannel;
import com.ruinscraft.chat.events.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.message.ActionChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class MeCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return true;
		}
		
		Player player = (Player) sender;
		ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
		ChatChannel<GenericChatMessage> focused = chatPlayer.getFocused();
		
		if (args.length < 1) {
			player.sendMessage(Constants.COLOR_BASE + "You must specify something to say");
			return true;
		}

		String message = String.join(" ", args);

		AsyncPlayerChatEvent event = new DummyAsyncPlayerChatEvent(true, player, message);

		Bukkit.getServer().getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return true;
		}
		
		if (focused.getPermission() != null && !player.hasPermission(focused.getPermission())) {
			chatPlayer.setFocused(ChatPlugin.getInstance().getChatChannelManager().getDefaultChatChannel());
		}

		if (!(focused instanceof DefaultLocalChatChannel)) {
			player.sendMessage(Constants.COLOR_ERROR + "You must be in a local chat channel to use /me");
			return true;
		}

		String senderPrefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
		String nickname = chatPlayer.getNickname();
		boolean allowColor = player.hasPermission(Constants.PERMISSION_COLORIZE_MESSAGES);
		ActionChatMessage chatMessage = new ActionChatMessage(senderPrefix, nickname, player.getUniqueId(), player.getName(), ChatPlugin.getInstance().getServerName(), focused.getName(), allowColor, message);

		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try {
				focused.dispatch(player, chatMessage, true).call();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}); 

		return true;
	}

}
