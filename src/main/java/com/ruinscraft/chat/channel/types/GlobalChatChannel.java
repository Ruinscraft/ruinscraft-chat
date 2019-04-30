package com.ruinscraft.chat.channel.types;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalChatChannel extends LabeledChatChannel<GenericChatMessage> {

    public GlobalChatChannel() {
        super("global", "Global Chat", null, ChatColor.WHITE, true, true, false);
    }

    @Override
    public String getLabel(GenericChatMessage context) {
        String base = "[G] ";
        String serverName = ChatPlugin.getInstance().getServerName();

        if (context.getServerSentFrom().equals(serverName)) {
            return ChatColor.GREEN + base;
        } else {
            return ChatColor.GRAY + base;
        }
    }

    @Override
    public String getFormat(String viewer, GenericChatMessage context) {
        String noColor = getLabel(context) + "&7[%prefix%&7] %sender% &8&l>&r" + getMessageColor() + " %message%";
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

                chatPlayer.setFocused(GlobalChatChannel.this);

                player.sendMessage(String.format(Constants.MESSAGE_FOCUSED_CHANNEL_SET_TO, "global"));

                return true;
            }
        };

        command.setLabel(getName());
        command.setUsage("/global");
        command.setDescription("Set your focused chat channel to global");
        command.setPermissionMessage(null);

        return command;
    }

}
