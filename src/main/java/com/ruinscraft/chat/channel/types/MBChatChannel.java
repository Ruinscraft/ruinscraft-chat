package com.ruinscraft.chat.channel.types;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;

public class MBChatChannel extends LabeledChatChannel<GenericChatMessage> {

	public MBChatChannel(String name, String prettyName, String permission, ChatColor messageColor, boolean logged,
			boolean mutable, boolean spyable) {
		super(name, prettyName, permission, messageColor, logged, mutable, spyable);
	}

	public MBChatChannel() {
		super("mb", "MB", "ruinscraft.chat.channel.mb", ChatColor.GREEN, true, false, false);
	}

	@Override
	public String getLabel(GenericChatMessage context) {
		return getMessageColor() + "[" + getPrettyName() + "] ";
	}

	@Override
	public String getFormat(String viewer, GenericChatMessage context) {
		String noColor = getLabel(context) + "&a[%server%] " + getMessageColor() + "[%prefix%" + getMessageColor() + "] %sender% &8&l>" + getMessageColor() + " %message%";
		return ChatColor.translateAlternateColorCodes('&', noColor);
	}

	@Override
	public Command getCommand() {
		Command command = new Command(getName()) {
			@Override
			public boolean execute(CommandSender sender, String commandLabel, String[] args) {
				if (!testPermission(sender)) {
					return true;
				}

				if (!(sender instanceof Player)) {
					return true;
				}

				Player player = (Player) sender;

				if (args.length < 1) {
					ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
					chatPlayer.setFocused(MBChatChannel.this);
					player.sendMessage(String.format(Constants.MESSAGE_FOCUSED_CHANNEL_SET_TO, MBChatChannel.this.getName()));
					return true;
				}

				String prefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
				String nickname = null;
				UUID uuid = player.getUniqueId();
				String name = player.getName();
				String server = ChatPlugin.getInstance().getServerName();
				String channel = getName();
				boolean colorize = true;
				String message = String.join(" ", args);

				GenericChatMessage chatMessage = new GenericChatMessage(prefix, nickname, uuid, name, server, channel, colorize, message);

				ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
					try {
						dispatch(player, chatMessage, false).call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

				return true;
			}
		};

		command.setUsage("/" + command.getLabel() + " <msg>");
		command.setPermission(getPermission());
		command.setPermissionMessage(null);

		return command;
	}

}
