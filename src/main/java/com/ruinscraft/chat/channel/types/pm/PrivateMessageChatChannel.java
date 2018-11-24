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
import com.ruinscraft.chat.message.ChatMessage;
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
					message = args[1]; // TODO: fix this
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
		Player recipient = Bukkit.getPlayerExact(chatMessage.getRecipient());
		
		if (recipient == null || !recipient.isOnline()) {
			return;
		}
		
		recipient.sendMessage(chatMessage.getSender() + " whispers " + chatMessage.getPayload());
		
		log(chatChannelManager, chatMessage);
	}

}
