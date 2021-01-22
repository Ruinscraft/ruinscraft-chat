package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChatCommand implements CommandExecutor, TabCompleter {

    private ChatPlugin chatPlugin;

    public ChatCommand(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        if (args.length < 1) {
            chatPlugin.getGuiManager().openGui(player, "chatsettings");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "togglefilter":
                toggleFilter(onlineChatPlayer);
                break;
            case "toggleallowdmsfromanyone":
                toggleAllowDmsFromAnyone(onlineChatPlayer);
                break;
            case "togglechannel":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "/" + label + " togglechannel <name>");
                    break;
                }
                String name = args[1];
                toggleMuteChannel(onlineChatPlayer, name);
                break;
            default:
                break;
        }

        return true;
    }

    private void toggleFilter(OnlineChatPlayer onlineChatPlayer) {
        boolean filter = !onlineChatPlayer.getPersonalizationSettings().isHideProfanity();

        if (filter) {
            onlineChatPlayer.sendMessage(ChatColor.GOLD + "You will no longer see messages with profane language.");
        } else {
            onlineChatPlayer.sendMessage(ChatColor.GOLD + "You will now see all messages.");
        }

        onlineChatPlayer.getPersonalizationSettings().setHideProfanity(filter);
        chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings());
    }

    private void toggleAllowDmsFromAnyone(OnlineChatPlayer onlineChatPlayer) {
        boolean allowDmsFromAnyone = !onlineChatPlayer.getPersonalizationSettings().isAllowDmsFromAnyone();

        if (allowDmsFromAnyone) {
            onlineChatPlayer.sendMessage(ChatColor.GOLD + "You have allowed direct messages from anyone (except people you've blocked).");
        } else {
            onlineChatPlayer.sendMessage(ChatColor.GOLD + "You have only allowed your friends to direct message you.");
        }

        onlineChatPlayer.getPersonalizationSettings().setAllowDmsFromAnyone(allowDmsFromAnyone);
        chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings());
    }

    private void toggleMuteChannel(OnlineChatPlayer onlineChatPlayer, String name) {
        if (onlineChatPlayer.getPersonalizationSettings().getMutedChannelDbNames().contains(name)) {
            onlineChatPlayer.getPersonalizationSettings().getMutedChannelDbNames().remove(name);

            onlineChatPlayer.sendMessage(ChatColor.GOLD + name + " is no longer muted.");
        } else {
            ChatChannel channel = chatPlugin.getChatChannelManager().getChannel(name);

            if (channel == null) {
                onlineChatPlayer.sendMessage(ChatColor.RED + "Unknown channel.");
            } else {
                if (onlineChatPlayer.getFocused(chatPlugin).equals(channel)) {
                    onlineChatPlayer.sendMessage(ChatColor.RED + "You are currently focused to this chat channel.");
                    return;
                }

                onlineChatPlayer.getPersonalizationSettings().getMutedChannelDbNames().add(name);

                onlineChatPlayer.sendMessage(ChatColor.GOLD + name + " is now muted.");
            }
        }

        chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings());
    }

    private static List<String> options = new ArrayList<>();

    static {
        options.add("togglefilter");
        options.add("toggleallowdmsfromanyone");
        options.add("togglechannel");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            for (String option : options) {
                if (option.toLowerCase().startsWith(args[0])) {
                    completions.add(option);
                }
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("togglechannel")) {
                for (ChatChannel channel : chatPlugin.getChatChannelManager().getChannels()) {
                    if (channel.getDatabaseName().toLowerCase().startsWith(args[1])) {
                        if (channel.getPermission() != null) {
                            if (!sender.hasPermission(channel.getPermission())) {
                                continue;
                            }
                        }
                        completions.add(channel.getDatabaseName());
                    }
                }
            }
        }

        return completions;
    }

}
