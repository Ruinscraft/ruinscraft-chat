package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import com.ruinscraft.chat.util.NetworkUtil;
import com.ruinscraft.chat.util.VaultUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

public abstract class ChatChannel {

    private ChatPlugin chatPlugin;
    private String pluginName;
    private String name;
    private String prefix;
    private ChatColor chatColor;
    private boolean crossServer;

    public ChatChannel(ChatPlugin chatPlugin, String pluginName, String name, String prefix, ChatColor chatColor, boolean crossServer) {
        this.chatPlugin = chatPlugin;
        this.pluginName = pluginName;
        this.name = name;
        this.prefix = prefix;
        this.chatColor = chatColor;
        this.crossServer = crossServer;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getName() {
        return name;
    }

    public String getDatabaseName() {
        return pluginName + ":" + name;
    }

    public String getPrefix() {
        return prefix;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public boolean isCrossServer() {
        return crossServer;
    }

    public String getPermission() {
        return getCommand(chatPlugin).getPermission();
    }

    public String format(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        StringJoiner stringJoiner = new StringJoiner(" ");
        stringJoiner.add(getPrefix() + ChatColor.RESET);
        stringJoiner.add(ChatColor.GRAY + "[" + VaultUtil.getPrefix(player) + ChatColor.GRAY + "]");
        stringJoiner.add(onlineChatPlayer.getPersonalizationSettings().getNameColor() + player.getName());
        stringJoiner.add(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + ">");
        boolean hasNickname = !onlineChatPlayer.getPersonalizationSettings().getNickname().equals("");
        if (hasNickname) {
            stringJoiner.add(ChatColor.GOLD + "(" + onlineChatPlayer.getPersonalizationSettings().getNickname() + ")");
        }
        stringJoiner.add(getChatColor() + chatMessage.getContent());

        return stringJoiner.toString();
    }

    public Command getCommand(ChatPlugin chatPlugin) {
        return new Command(getName()) {
            @Override
            public boolean execute(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    return false;
                }

                Player player = (Player) sender;
                OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

                if (args.length < 1) {
                    ChatChannel oldFocused = onlineChatPlayer.getFocused(chatPlugin);

                    // Switch focused channel
                    onlineChatPlayer.setFocused(ChatChannel.this);

                    CompletableFuture.runAsync(() -> {
                        if (ChatChannel.this instanceof GlobalChatChannel) {
                            chatPlugin.getChatStorage().deleteActiveChannel(onlineChatPlayer, oldFocused).join();
                        } else {
                            chatPlugin.getChatStorage().insertActiveChannel(onlineChatPlayer, ChatChannel.this).join();
                        }

                        onlineChatPlayer.sendMessage(ChatColor.GOLD + "Channel switched to: " + ChatChannel.this.getName());
                    });
                } else {
                    // Send message to channel without switching focused
                    String message = String.join(" ", Arrays.copyOfRange(args, 0, args.length));
                    ChatMessage chatMessage = new ChatMessage(onlineChatPlayer, message, chatPlugin.getServerId(), getDatabaseName());

                    chatPlugin.getChatStorage().saveChatMessage(chatMessage)
                            .thenRun(() -> NetworkUtil.sendChatEventPacket(chatPlugin, player, chatPlugin, chatMessage.getId()));
                }

                return true;
            }
        };
    }

    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        return Bukkit.getOnlinePlayers();
    }

}
