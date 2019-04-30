package com.ruinscraft.chat.channel.types;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.message.ActionChatMessage;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class DefaultLocalChatChannel extends LabeledChatChannel<GenericChatMessage> {

    private static final ChatColor DEFAULT_LOCAL_COLOR = ChatColor.GRAY;

    public DefaultLocalChatChannel() {
        super("local", "Local Chat (the server you are on)", null, ChatColor.YELLOW, true, true, true);
    }

    @Override
    public String getLabel(GenericChatMessage context) {
        return ChatColor.AQUA + "[L] ";
    }

    @Override
    public String getFormat(String viewer, GenericChatMessage context) {
        String noColor = "";
        if (context.getSenderNickname() != null) {
            if (context instanceof ActionChatMessage) {
                noColor = getLabel(context) + "&d*%sender% (%nickname%) %message%*";
            } else {
                noColor = getLabel(context) + "&7[%prefix%&7] %sender% &8&l>&r &6(%nickname%)&r" + getMessageColor() + " %message%";
            }
        } else {
            if (context instanceof ActionChatMessage) {
                noColor = getLabel(context) + "&d*%sender% %message%*";
            } else {
                noColor = getLabel(context) + "&7[%prefix%&7] %sender% &8&l>&r" + getMessageColor() + " %message%";
            }
        }
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

                ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

                /* Requires permission */
                if (commandLabel.toLowerCase().equals("localcolor")) {
                    if (!player.hasPermission("ruinscraft.command.localcolor")) {
                        player.sendMessage(Constants.COLOR_ERROR + "You do not have permission to use this.");
                        return true;
                    }

                    if (args.length < 1) {
                        if (!chatPlayer.hasMeta("localcolor")) {
                            player.sendMessage(Constants.COLOR_BASE + "You currently do not have a local color set");
                            player.sendMessage(Constants.COLOR_BASE + "Set one with /" + commandLabel + " <colorcode>");
                        } else {
                            char colorCode = chatPlayer.getMeta("localcolor").charAt(0);
                            player.sendMessage(Constants.COLOR_BASE + "Your current local color is " + ChatColor.getByChar(colorCode) + ChatColor.getByChar(colorCode).name());
                            player.sendMessage(Constants.COLOR_BASE + "You can reset it with /localcolorreset");
                        }

                        player.sendMessage(Constants.COLOR_BASE + "Color codes:");
                        player.sendMessage(ChatColor.DARK_RED + "4 " + ChatColor.RED + "c " + ChatColor.GOLD + "6 " + ChatColor.YELLOW + "e " + ChatColor.DARK_GREEN + "2 " + ChatColor.GREEN + "a " + ChatColor.AQUA + "b " + ChatColor.DARK_AQUA + "3 " + ChatColor.DARK_BLUE + "1 " + ChatColor.BLUE + "9 " + ChatColor.LIGHT_PURPLE + "d " + ChatColor.DARK_PURPLE + "5 " + ChatColor.WHITE + "f " + ChatColor.GRAY + "7 " + ChatColor.DARK_GRAY + "8 " + ChatColor.BLACK + "0");
                        return true;
                    }

                    char newCode = args[0].charAt(0);

                    ChatColor newChatColor = ChatColor.getByChar(newCode);

                    if (newChatColor == null || !newChatColor.isColor()) {
                        player.sendMessage(Constants.COLOR_ERROR + "Invalid color code");
                        return true;
                    }

                    chatPlayer.setMeta("localcolor", Character.toString(newCode));
                    player.sendMessage(Constants.COLOR_BASE + "Local color set to " + newChatColor + newChatColor.name());

                    return true;
                }

                /* Requires no permission */
                else if (commandLabel.toLowerCase().equals("localcolorreset")) {
                    player.sendMessage(Constants.COLOR_BASE + "Local chat color reset");
                    chatPlayer.setMeta("localcolor", null);
                    return true;
                }

                chatPlayer.setFocused(DefaultLocalChatChannel.this);

                player.sendMessage(String.format(Constants.MESSAGE_FOCUSED_CHANNEL_SET_TO, "local"));

                return true;
            }
        };

        command.setAliases(Arrays.asList("localcolor", "localcolorreset"));
        command.setLabel(getName());
        command.setUsage("/local");
        command.setDescription("Set your focused chat channel to local");
        command.setPermissionMessage(null);

        return command;
    }

    @Override
    public void sendToChat(GenericChatMessage chatMessage) {
        /* Check if the server is supposed to see this message */
        if (ChatPlugin.getInstance().getServerName() == null) {
            return;
        }

        if (!ChatPlugin.getInstance().getServerName().equals(chatMessage.getServerSentFrom())) {
            return;
        }

        ChatPlayer chatPlayerSender = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(chatMessage.getSenderUUID());

        if (chatPlayerSender == null) {
            return;
        }

        ChatColor localColor = DEFAULT_LOCAL_COLOR;

        if (chatPlayerSender.hasMeta("localcolor")) {
            localColor = ChatColor.getByChar(chatPlayerSender.getMeta("localcolor").charAt(0));
        }

        for (Player player : getIntendedRecipients(chatMessage)) {
            /* Send the message to the player if the permission checks out */
            String format = getFormat(player.getName(), chatMessage);

            format = format.replace("%prefix%", ChatColor.translateAlternateColorCodes('&', chatMessage.getSenderPrefix()));

            /* Don't show localcolor if ActionChatMessage */
            if (chatMessage instanceof ActionChatMessage) {
                format = format.replace("%sender%", chatMessage.getSender());
            } else {
                format = format.replace("%sender%", localColor + chatMessage.getSender());
            }

            if (chatMessage.getSenderNickname() != null) {
                format = format.replace("%nickname%", chatMessage.getSenderNickname());
            }

            if (chatMessage.colorizePayload()) {
                format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', chatMessage.getPayload()));
            } else {
                format = format.replace("%message%", chatMessage.getPayload());
            }

            player.sendMessage(format);
        }
    }

}
