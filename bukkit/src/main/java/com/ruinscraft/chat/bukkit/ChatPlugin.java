package com.ruinscraft.chat.bukkit;

import com.ruinscraft.chat.api.ChatConfig;
import com.ruinscraft.chat.core.Chat;
import com.ruinscraft.chat.core.ChatPlatform;
import com.ruinscraft.chat.core.channel.ChatChannel;
import com.ruinscraft.chat.core.player.ChatPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ChatPlugin extends JavaPlugin implements ChatPlatform {

    private Chat chat;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        chat = new Chat(this, getDefaultChannel(), getChannels());

        // Register listeners
        getServer().getPluginManager().registerEvents(new JoinQuitListener(chat.getPlayerManager()), this);
        getServer().getPluginManager().registerEvents(new ChatListener(chat), this);

        VaultUtil.init();
    }

    @Override
    public void onDisable() {
        chat.getMessageManager().close();
    }

    public Chat getChat() {
        return chat;
    }

    @Override
    public ChatConfig loadConfig() {
        ChatConfig config = new ChatConfig();

        config.mysqlHost = getConfig().getString("storage.mysql.host");
        config.mysqlPort = getConfig().getInt("storage.mysql.port");
        config.mysqlDatabase = getConfig().getString("storage.mysql.database");
        config.mysqlUsername = getConfig().getString("storage.mysql.username");
        config.mysqlPassword = getConfig().getString("storage.mysql.password");

        config.redisHost = getConfig().getString("messaging.redis.host");
        config.redisPort = getConfig().getInt("messaging.redis.port");

        return config;
    }

    @Override
    public void run(Runnable runnable) {
        getServer().getScheduler().runTask(this, runnable);
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void runAsyncRepeat(Runnable runnable, long delayTicks, long periodTicks) {
        getServer().getScheduler().runTaskTimerAsynchronously(this, runnable, delayTicks, periodTicks);
    }

    @Override
    public void log(String message) {
        getLogger().info(message);
    }

    @Override
    public void warn(String message) {
        getLogger().warning(message);
    }

    @Override
    public Set<ChatPlayer> getChatPlayers() {
        Set<ChatPlayer> chatPlayers = new HashSet<>();

        for (Player player : getServer().getOnlinePlayers()) {
            ChatPlayer chatPlayer = chat.getPlayerManager().get(player.getUniqueId());

            chatPlayers.add(chatPlayer);
        }

        return chatPlayers;
    }

    @Override
    public ChatPlayer createChatPlayer(UUID mojangId) {
        return new BukkitChatPlayer(getServer().getPlayer(mojangId));
    }

    protected abstract ChatChannel getDefaultChannel();

    protected abstract Set<ChatChannel> getChannels();

}
