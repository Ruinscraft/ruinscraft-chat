package com.ruinscraft.chat.channel.types.pm;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.channel.ChatChannelManager;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.playerstatus.PlayerStatus;
import com.ruinscraft.playerstatus.PlayerStatusPlugin;

public class PrivateMessageChatChannel implements ChatChannel<PrivateChatMessage> {

	private ReplyStorage replyStorage;

	public PrivateMessageChatChannel(ConfigurationSection replySection) {
		ConfigurationSection storageSection = replySection.getConfigurationSection("storage");

		if (storageSection.getBoolean("redis.use")) {
			replyStorage = new RedisReplyStorage(storageSection.getConfigurationSection("redis"));
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
	public String getFormat(String viewer, PrivateChatMessage context) {
		/* sent to themself */
		if (context.getSender().equals(context.getRecipient())) {
			return ChatColor.DARK_AQUA + "(you say to yourself)" + getMessageColor() + " '%message%'";
		}
		
		/* viewer is the sender */
		if (viewer.equals(context.getSender())) {
			return ChatColor.DARK_AQUA + "[to: %recipient%]" + getMessageColor() + " %message%";
		}
		
		/* viewer is the recipient */
		else if (viewer.equals(context.getRecipient())) {
			return ChatColor.DARK_AQUA + "[from: %sender%]" + getMessageColor() + " %message%";
		}
		
		/* some default format */
		else {
			return "[%sender% -> %recipient%] %message%";
		}
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
	public Command getCommand() {
		Command command = new Command(getName()) {
			@Override
			public boolean execute(CommandSender sender, String commandLabel, String[] args) {
				String message = null;
				String recipient = null;
				boolean reply = false;

				switch (commandLabel.toLowerCase()) {
				case "r":
				case "reply":
					reply = true;
				default:
					break;
				}

				if (reply) {
					if (args.length < 1) {
						sender.sendMessage(getUsage());
						return true;
					}

					Callable<String> callable = replyStorage.getReply(sender.getName());

					try {
						recipient = callable.call();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (recipient == null) {
						sender.sendMessage("No one to reply to");
						return true;
					}

					message = String.join(" ", args);
				} else {
					if (args.length < 2) {
						sender.sendMessage(getUsage());
						return true;
					}

					recipient = args[0];
					message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
				}

				replyStorage.setReply(sender.getName(), recipient);

				PrivateChatMessage pm = new PrivateChatMessage(sender.getName(), recipient, getName(), message);

				dispatch(ChatPlugin.getInstance().getMessageManager().getDispatcher(), sender, pm);

				return true;
			}
		};

		command.setAliases(Arrays.asList(
				"message",
				"msg",
				"m",
				"whisper",
				"w",
				"tell",
				"t",
				"pm",
				"reply",
				"r"
				));

		command.setLabel(getName());

		// TODO: fix this for replies
		command.setUsage(command.getLabel() + " message");

		command.setDescription("Message or reply to someone on the server");

		return command;
	}

	@Override
	public boolean isLogged() {
		return true;
	}

	@Override
	public void dispatch(MessageDispatcher dispatcher, CommandSender caller, PrivateChatMessage chatMessage) {
		Callable<PlayerStatus> callable = PlayerStatusPlugin.getAPI().getPlayerStatus(chatMessage.getRecipient());

		ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
			try {
				PlayerStatus recipientStatus = callable.call();

				if (caller instanceof Player) {
					Player callerPlayer = (Player) caller;

					if (!callerPlayer.isOnline()) {
						return;
					}
				}

				if (!recipientStatus.isOnline()) {
					caller.sendMessage(ChatColor.RED + chatMessage.getRecipient() + " is not online.");
					return;
				}

				Message dispatchable = new Message(chatMessage);

				dispatcher.dispatch(dispatchable);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void sendToChat(ChatChannelManager chatChannelManager, PrivateChatMessage chatMessage) {
		Player sender = Bukkit.getPlayerExact(chatMessage.getSender());
		Player recipient = Bukkit.getPlayerExact(chatMessage.getRecipient());
		boolean log = false;
		
		if (sender != null) {
			if (sender == recipient) {
				// sending to themself
				String format = getFormat(chatMessage.getRecipient(), chatMessage);
				recipient.sendMessage(format(format, chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload()));
				log = true;
				return;
			}
			
			if (sender.isOnline()) {
				// viewer is the sender
				String format = getFormat(chatMessage.getSender(), chatMessage);
				sender.sendMessage(format(format, chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload()));
				log = true;
			}
		}

		if (recipient != null) {
			if (recipient.isOnline()) {
				// viewer is the recipient
				String format = getFormat(chatMessage.getRecipient(), chatMessage);
				recipient.sendMessage(format(format, chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload()));
				log = true;
			}
		}
		
		if (log) {
			log(chatChannelManager, chatMessage);
		}
	}
	
	private static String format(String format, String sender, String recipient, String message) {
		return format
				.replace("%sender%", sender)
				.replace("%recipient%", recipient)
				.replace("%message%", message);
	}

}
