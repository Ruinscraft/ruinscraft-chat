package com.ruinscraft.chat.listeners;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.events.DummyAsyncPlayerChatEvent;
import com.ruinscraft.chat.events.FullAsyncPlayerChatEvent;
import com.ruinscraft.chat.filters.ChatFilter;
import com.ruinscraft.chat.filters.ChatFilterManager;
import com.ruinscraft.chat.filters.NotSendableException;
import com.ruinscraft.chat.message.GenericChatMessage;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private static ChatPlugin chatPlugin = ChatPlugin.getInstance();

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (event instanceof DummyAsyncPlayerChatEvent) {
            return;
        }

        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String payload = event.getMessage();

        if (payload.isEmpty()) {
            return;
        }

        if (ChatPlugin.getInstance().getConfig().getBoolean("channels.disable") || event instanceof FullAsyncPlayerChatEvent) {
            final ChatFilterManager chatFilterManager = ChatPlugin.getInstance().getChatFilterManager();
            for (final ChatFilter filter : chatFilterManager.getChatFilters()) {
                try {
                    event.setMessage(filter.filter(event.getMessage()));
                } catch (NotSendableException e) {
                    player.sendMessage(ChatColor.RED + e.getMessage());
                    event.setCancelled(true);
                }
            }
            return;
        }

        event.setCancelled(true);

        ChatPlayer chatPlayer = chatPlugin.getChatPlayerManager().getChatPlayer(player.getUniqueId());
        ChatChannel<GenericChatMessage> chatChannel = chatPlayer.getFocused();

        if (chatChannel.getPermission() != null && !player.hasPermission(chatChannel.getPermission())) {
            chatPlayer.setFocused(chatPlugin.getChatChannelManager().getDefaultChatChannel());
        }

        String senderPrefix = ChatPlugin.getVaultChat().getPlayerPrefix(player);
        String nickname = chatPlayer.getNickname();
        boolean allowColor = player.hasPermission(Constants.PERMISSION_COLORIZE_MESSAGES);
        GenericChatMessage chatMessage = new GenericChatMessage(senderPrefix, nickname, player.getUniqueId(), player.getName(), chatPlugin.getServerName(), chatChannel.getName(), allowColor, payload);

        try {
            /* Safe because this is already async */
            chatChannel.dispatch(player, chatMessage, true).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
