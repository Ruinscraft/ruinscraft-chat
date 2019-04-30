package com.ruinscraft.chat.commands;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NicknameResetCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;
        ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

        chatPlayer.setNickname(null);
        player.sendMessage(Constants.COLOR_BASE + "Your nickname has been reset");

        return true;
    }

}
