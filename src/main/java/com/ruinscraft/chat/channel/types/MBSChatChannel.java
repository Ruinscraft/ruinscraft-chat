package com.ruinscraft.chat.channel.types;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.message.GenericChatMessage;

public class MBSChatChannel implements ChatChannel<GenericChatMessage> {

	@Override
	public String getName() {
		return "mbs";
	}

	@Override
	public String getFormat(String viewer, GenericChatMessage context) {
		String noColor = "&6[MBS] &a[%server%] &6[%prefix%&6] %sender% &8&l>" + getMessageColor() + " %message%";
		return ChatColor.translateAlternateColorCodes('&', noColor);
	}

	@Override
	public ChatColor getMessageColor() {
		return ChatColor.GOLD;
	}

	@Override
	public String getPermission() {
		return "ruinscraft.chat.channel.mbs";
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
					player.sendMessage(getUsage());
					return true;
				}
				
				String prefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
				String nickname = null;
				String name = player.getName();
				String server = ChatPlugin.getInstance().getServerName();
				String channel = getName();
				boolean colorize = true;
				String message = String.join(" ", args);
				
				GenericChatMessage chatMessage = new GenericChatMessage(prefix, nickname, name, server, channel, colorize, message);
				
				ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
					try {
						dispatch(ChatPlugin.getInstance().getMessageManager().getDispatcher(), player, false, chatMessage).call();
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

	@Override
	public boolean isLogged() {
		return false;
	}
	
	@Override
	public boolean isLoggedGlobally() {
		return false;
	}

}
