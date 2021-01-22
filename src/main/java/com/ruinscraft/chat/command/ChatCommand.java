package com.ruinscraft.chat.command;

import com.ruinscraft.chat.ChatPlugin;
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
            // TODO: gui
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "togglefilter":
                toggleFilter(onlineChatPlayer);
                break;
            case "toggleallowdmsfromanyone":
                toggleAllowDmsFromAnyone(onlineChatPlayer);
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

    private static List<String> options = new ArrayList<>();

    static {
        options.add("togglefilter");
        options.add("toggleallowdmsfromanyone");
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

        return completions;
    }

}
