package com.ruinscraft.chat.channel.types.pm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.ChatUtil;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.events.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.message.PrivateChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageDispatcher;
import com.ruinscraft.chat.players.ChatPlayer;
import com.ruinscraft.playerstatus.PlayerStatus;
import com.ruinscraft.playerstatus.PlayerStatusPlugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

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
	public String getPrettyName() {
		return "Private Messages";
	}

	@Override
	public String getFormat(String viewer, PrivateChatMessage context) {
		/* sent to themself */
		if (context.getSender().equals(context.getRecipient())) {
			return ChatColor.DARK_AQUA + "(you say to yourself)" + getMessageColor() + " %message%";
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
			public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
				List<String> players = new ArrayList<>();
				
				if (args.length < 1) {
					return players;
				}

				String partialName = args[0];
				List<String> allOnlinePlayers = PlayerStatusPlugin.getAPI().getOnlyPlayers();
				
				
				for (String player : allOnlinePlayers) {
					if (player.toLowerCase().startsWith(partialName.toLowerCase())) {
						players.add(player);
					}
				}
				
				return players;
			}

			@Override
			public boolean execute(CommandSender sender, String commandLabel, String[] args) {
				ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
					if (!(sender instanceof Player)) {
						return;
					}

					Player player = (Player) sender;

					if (!testPermission(player)) {
						return;
					}

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
							player.sendMessage("/reply <msg>");
							return;
						}

						try {
							recipient = replyStorage.getReply(player.getName()).call();
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (recipient == null) {
							player.sendMessage(Constants.COLOR_BASE + "No one to reply to");
							return;
						}

						message = String.join(" ", args);
					} else {
						if (args.length < 2) {
							player.sendMessage(getUsage());
							return;
						}

						recipient = args[0];
						message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					}

					try {
						replyStorage.setReply(player.getName(), recipient).call();
					} catch (Exception e) {
						e.printStackTrace();
					}

					String senderPrefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
					String nickname = null;
					String name = sender.getName();
					String server = ChatPlugin.getInstance().getServerName();
					String channel = getName();
					boolean colorize = sender.hasPermission(Constants.PERMISSION_COLORIZE_MESSAGES);

					PrivateChatMessage pm = new PrivateChatMessage(senderPrefix, nickname, name, recipient, server, channel, colorize, message);

					try {
						dispatch(ChatPlugin.getInstance().getMessageManager().getDispatcher(), player, true, pm).call();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});

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
		command.setUsage("/" + command.getLabel() + " <name> <msg>");
		command.setDescription("Message or reply to someone on the server");
		command.setPermissionMessage(null);

		return command;
	}

	@Override
	public boolean isLogged() {
		return true;
	}

	@Override
	public boolean isLoggedGlobally() {
		return true;
	}
	
	@Override
	public boolean muteable() {
		return true;
	}
	
	@Override
	public boolean spyable() {
		return true;
	}

	@Override
	public Callable<Void> dispatch(MessageDispatcher dispatcher, Player player, boolean filter, PrivateChatMessage chatMessage) {
		return new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				AsyncPlayerChatEvent event = new DummyAsyncPlayerChatEvent(false, player, chatMessage.getPayload());

				Bukkit.getServer().getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					return null;
				}

				ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
				
				if (chatPlayer.isMuted(PrivateMessageChatChannel.this)) {
					player.sendMessage(Constants.COLOR_ERROR + "You have this channel muted. Unmute it with /chat");
					return null;
				}
				
				try {
					PlayerStatus recipientStatus = PlayerStatusPlugin.getAPI().getPlayerStatus(chatMessage.getRecipient()).call();

					if (!player.isOnline()) {
						return null;
					}

					if (!recipientStatus.isOnline()) {
						player.sendMessage(Constants.COLOR_ACCENT + chatMessage.getRecipient() + Constants.COLOR_BASE + " is not online.");
						return null;
					}

					if (filter) {
						try {
							filter(ChatPlugin.getInstance().getChatChannelManager(), ChatPlugin.getInstance().getChatFilterManager(), player, chatMessage).call();
						} catch (NotSendableException e) {
							if (player != null) {
								player.sendMessage(Constants.COLOR_ERROR + e.getMessage());
								return null;
							}
						}
					}

					Message dispatchable = new Message(chatMessage);

					dispatcher.dispatch(dispatchable);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

	@Override
	public void sendToChat(PrivateChatMessage chatMessage) {
		Player sender = Bukkit.getPlayerExact(chatMessage.getSender());
		Player recipient = Bukkit.getPlayerExact(chatMessage.getRecipient());
		boolean log = false;

		if (sender != null) {
			if (sender == recipient) {
				// sending to themself
				String format = replace(getFormat(chatMessage.getRecipient(), chatMessage), chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload());
				TextComponent toSend = new TextComponent(ChatUtil.convertFromLegacy(format));
				toSend.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, ChatUtil.convertFromLegacy("(crazy person)")));
				recipient.spigot().sendMessage(toSend);
				log = true;
				return;
			}

			if (sender.isOnline()) {
				// viewer is the sender
				String format = replace(getFormat(chatMessage.getSender(), chatMessage), chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload());
				TextComponent toSend = new TextComponent(ChatUtil.convertFromLegacy(format));
				sender.spigot().sendMessage(toSend);
				log = true;
			}
		}

		if (recipient != null) {
			if (recipient.isOnline()) {
				// viewer is the recipient
				ChatPlayer recipientChatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(recipient.getUniqueId());
				
				if (recipientChatPlayer.isMuted(this)) {
					return;
				}
				
				boolean ignoring = false;

				if (recipientChatPlayer.isIgnoring(chatMessage.getSender())) {
					ignoring = true;
				}
				
				OfflinePlayer potentialOfflinePlayer = Bukkit.getOfflinePlayer(chatMessage.getSender());

				if (potentialOfflinePlayer != null && recipientChatPlayer.isIgnoring(potentialOfflinePlayer.getUniqueId())) {
					ignoring = true;
				}

				if (ignoring) {
					return;
				}
				
				String format = replace(getFormat(chatMessage.getRecipient(), chatMessage), chatMessage.getSender(), chatMessage.getRecipient(), chatMessage.getPayload());
				TextComponent toSend = new TextComponent(ChatUtil.convertFromLegacy(format));
				toSend.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, ChatUtil.convertFromLegacy("sent from " + chatMessage.getServerSentFrom())));
				recipient.spigot().sendMessage(toSend);
				recipient.spigot().sendMessage(ChatMessageType.ACTION_BAR, ChatUtil.convertFromLegacy(Constants.COLOR_ACCENT + "new message from " + chatMessage.getSender()));
				log = true;
			}
		}

		if (log) {
			logAsync(chatMessage);
		}
	}

	private static String replace(String format, String sender, String recipient, String message) {
		return format
				.replace("%sender%", sender)
				.replace("%recipient%", recipient)
				.replace("%message%", message);
	}

}
