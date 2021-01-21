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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("togglefilter");
        }

        return options;
    }

}
