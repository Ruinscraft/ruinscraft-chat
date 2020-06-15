package com.ruinscraft.chat.bukkit.commands;

import com.ruinscraft.chat.api.IChatPlayer;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.command.ListCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListCommandExecutor implements CommandExecutor {

    private Chat chat;
    private ListCommand listCommand;

    public ListCommandExecutor(Chat chat) {
        this.chat = chat;
        listCommand = new ListCommand(chat);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        IChatPlayer chatPlayer = chat.getChatPlayer(player.getUniqueId());

        listCommand.onExecute(chatPlayer, label, args);

        return true;
    }

}
