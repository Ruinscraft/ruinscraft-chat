package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.NetworkUtil;
import com.ruinscraft.chat.VaultUtil;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public abstract class ChatChannel {

    private String pluginName;
    private String name;
    private String prefix;
    private ChatColor chatColor;
    private boolean crossServer;

    public ChatChannel(String pluginName, String name, String prefix, ChatColor chatColor, boolean crossServer) {
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

    public String format(ChatMessage chatMessage) {
        Player player = Bukkit.getPlayer(chatMessage.getSender().getMojangId());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPrefix() + ChatColor.RESET);
        stringBuilder.append(ChatColor.GRAY + "[" + ChatColor.RESET + VaultUtil.getPrefix(player) + ChatColor.GRAY + "]" + ChatColor.RESET + " ");
        stringBuilder.append(ChatColor.GRAY + player.getName() + ChatColor.RESET + " ");
        stringBuilder.append(ChatColor.DARK_GRAY + ChatColor.BOLD.toString() + "> " + getChatColor() + chatMessage.getContent());

        return stringBuilder.toString();
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
                    ChatMessage chatMessage = new ChatMessage(chatPlugin, onlineChatPlayer, ChatChannel.this, message);

                    chatPlugin.getChatStorage().saveChatMessage(chatMessage)
                            .thenRun(() -> NetworkUtil.sendChatEventPacket(player, chatPlugin, chatMessage.getId()));
                }

                return true;
            }
        };
    }

    public Collection<? extends Player> getRecipients(ChatMessage chatMessage) {
        return Bukkit.getOnlinePlayers();
    }

}
