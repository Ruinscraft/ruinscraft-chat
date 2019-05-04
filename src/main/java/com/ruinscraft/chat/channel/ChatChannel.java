package com.ruinscraft.chat.channel;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.ChatFilterManager;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.message.ChatMessage;
import com.ruinscraft.chat.messenger.Message;
import com.ruinscraft.chat.messenger.MessageManager;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public abstract class ChatChannel<T extends ChatMessage> {

    private String name;
    private String prettyName;
    private String permission;
    private ChatColor messageColor;
    private boolean logged;
    private boolean mutable;
    private boolean spyable;

    public ChatChannel(String name, String prettyName, String permission, ChatColor messageColor, boolean logged, boolean mutable, boolean spyable) {
        this.name = name;
        this.prettyName = prettyName;
        this.permission = permission;
        this.messageColor = messageColor;
        this.logged = logged;
        this.mutable = mutable;
        this.spyable = spyable;
    }

    public String getName() {
        return name;
    }

    public String getPrettyName() {
        return prettyName;
    }

    public String getPermission() {
        return permission;
    }

    public ChatColor getMessageColor() {
        return messageColor;
    }

    public boolean isLogged() {
        return logged;
    }

    public boolean isMutable() {
        return mutable;
    }

    public boolean isSpyable() {
        return spyable;
    }

    public abstract String getFormat(String viewer, T context);

    public abstract Command getCommand();

    public Callable<Void> filter(Player player, T chatMessage) throws NotSendableException {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ChatFilterManager chatFilterManager = ChatPlugin.getInstance().getChatFilterManager();

                for (ChatFilter filter : chatFilterManager.getChatFilters()) {
                    chatMessage.setPayload(filter.filter(chatMessage.getPayload()));
                }

                return null;
            }
        };
    }

    public Callable<Boolean> sanitize(Player player, T chatMessage, boolean filter) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (player == null || !player.isOnline()) {
                    return false;
                }

                if (chatMessage == null) {
                    return false;
                }

                ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

                /* Check if the ChatPlayer has the ChatChannel muted */
                if (chatPlayer.isMuted(ChatChannel.this)) {
                    player.sendMessage(Constants.COLOR_ERROR + "You have this chat channel muted. Unmute it with /chat");
                    return false;
                }

                /* Check if the ChatMessage throws NotSendableException */
                if (filter) {
                    ChatFilterManager chatFilterManager = ChatPlugin.getInstance().getChatFilterManager();

                    for (ChatFilter filter : chatFilterManager.getChatFilters()) {
                        try {
                            chatMessage.setPayload(filter.filter(chatMessage.getPayload()));
                        } catch (NotSendableException e) {
                            player.sendMessage(Constants.COLOR_ERROR + e.getMessage());
                            return false;
                        }
                    }
                }

                return true;
            }
        };
    }

    public Callable<Void> dispatch(Player player, T chatMessage, boolean filter) {
        return new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (!sanitize(player, chatMessage, filter).call()) {
                    return null;
                }
                Message message = new Message(chatMessage);
                MessageManager messageManager = ChatPlugin.getInstance().getMessageManager();
                messageManager.getDispatcher().dispatch(message);
                logAsync(chatMessage);
                return null;
            }
        };
    }

    public boolean canSee(ChatPlayer chatPlayer, T chatMessage) {
        if (chatPlayer == null) {
            ChatPlugin.warning("chatPlayer null");
            return false;
        }

        if (chatPlayer.isMuted(this)) {
            return false;
        }

        if (chatPlayer.isIgnoring(chatMessage.getSender())) {
            return false;
        }

        if (chatPlayer.isIgnoring(chatMessage.getSenderUUID())) {
            return false;
        }

        return true;
    }

    public Collection<? extends Player> getIntendedRecipients(T context) {
        Set<Player> recipients = new HashSet<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOnline()) {
                continue;
            }

            ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

            if (!canSee(chatPlayer, context)) {
                continue;
            }

            if (permission != null && !player.hasPermission(permission)) {
                continue;
            }

            recipients.add(player);
        }

        return recipients;
    }

    public void sendToChat(T chatMessage) {
        for (Player player : getIntendedRecipients(chatMessage)) {
            String format = getFormat(player.getName(), chatMessage);

            format = format
                    .replace("%server%", chatMessage.getServerSentFrom())
                    .replace("%prefix%", ChatColor.translateAlternateColorCodes('&', chatMessage.getSenderPrefix()))
                    .replace("%sender%", chatMessage.getSender());

            if (chatMessage.colorizePayload()) {
                format = format.replace("%message%", ChatColor.translateAlternateColorCodes('&', chatMessage.getPayload()));
            } else {
                format = format.replace("%message%", chatMessage.getPayload());
            }

            player.sendMessage(format);
        }
    }

    public void logAsync(T chatMessage) {
        if (!logged) {
            return;
        }

        ChatPlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ChatPlugin.getInstance(), () -> {
            ChatPlugin.getInstance().getChatLoggingManager().getChatLoggers().forEach(logger -> {
                try {
                    logger.log(chatMessage).call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void registerCommands() {
        /* For compatibility with Essentials, etc */
        unregisterCommands();

        if (getCommand() == null) {
            return;
        }

        try {
            Field bukkitCommandMap = ChatPlugin.getInstance().getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(ChatPlugin.getInstance().getServer());
            commandMap.register(Constants.STRING_RUINSCRAFT_CHAT_PLUGIN_NAME, getCommand());
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void unregisterCommands() {
        if (!ChatPlugin.is_112()) {
            return;
        }

        if (getCommand() == null) {
            return;
        }

        Plugin plugin = ChatPlugin.getInstance();

        try {
            Field bukkitCommandMap = plugin.getServer().getClass().getDeclaredField("commandMap");
            bukkitCommandMap.setAccessible(true);

            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(plugin.getServer());

            getCommand().unregister(commandMap);

            Field commandMapKnownCommands = commandMap.getClass().getDeclaredField("knownCommands");
            commandMapKnownCommands.setAccessible(true);

            HashMap<String, Command> knownCommands = (HashMap<String, Command>) commandMapKnownCommands.get(commandMap);

            knownCommands.remove(getCommand().getName());

            for (String alias : getCommand().getAliases()) {
                knownCommands.remove(alias);
            }
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
