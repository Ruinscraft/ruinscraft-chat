package com.ruinscraft.chat.command.completers;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.FriendRequest;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FriendsCompleter implements TabCompleter {

    private ChatPlugin chatPlugin;

    public FriendsCompleter(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return new ArrayList<>();
        }

        Player player = (Player) sender;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);
        List<String> friendUsernames = new ArrayList<>();

        for (FriendRequest friendRequest : onlineChatPlayer.getFriendRequests()) {
            if (friendRequest.isAccepted()) {
                String username = friendRequest.getOther(onlineChatPlayer).getMinecraftUsername();
                friendUsernames.add(username);
            }
        }

        return friendUsernames;
    }

}
