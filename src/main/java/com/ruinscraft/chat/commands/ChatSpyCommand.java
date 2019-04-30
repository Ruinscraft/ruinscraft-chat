package com.ruinscraft.chat.commands;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.ChatUtil;
import com.ruinscraft.chat.Constants;
import com.ruinscraft.chat.channel.ChatChannel;
import com.ruinscraft.chat.players.ChatPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Set;

/**
 * A command to allow privileged Players to "spy" on certain chat channels.
 * Brings up a GUI much like the ChatCommand to enable/disable spying on channels.
 */
public class ChatSpyCommand implements CommandExecutor, Listener {

    private static final String INVENTORY_NAME = "Chat Spy Menu";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;
        ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());
        Set<ChatChannel<?>> spyableChannels = ChatPlugin.getInstance().getChatChannelManager().getSpyableChannels();

        int slots = ChatUtil.getInventorySlotCount(spyableChannels.size());
        Inventory chatMenu = Bukkit.createInventory(null, slots, INVENTORY_NAME);
        ;

        for (ChatChannel<?> channel : spyableChannels) {
            ItemStack channelItem = new ItemStack(Material.GRASS);
            ItemMeta channelItemMeta = channelItem.getItemMeta();
            channelItemMeta.setDisplayName(channel.getPrettyName());
            channelItem.setItemMeta(channelItemMeta);
            chatMenu.addItem(channelItem);
        }

        for (ItemStack itemStack : chatMenu.getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) {
                continue;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!chatPlayer.isSpying(itemMeta.getDisplayName())) {
                itemMeta.setDisplayName("Spy on " + itemMeta.getDisplayName());
            } else {
                itemMeta.setDisplayName("Stop spying on " + itemMeta.getDisplayName());
            }

            itemStack.setItemMeta(itemMeta);
        }

        player.openInventory(chatMenu);

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        ChatPlayer chatPlayer = ChatPlugin.getInstance().getChatPlayerManager().getChatPlayer(player.getUniqueId());

        if (player.getOpenInventory() == null) return;

        if (player.getOpenInventory().getTitle().equals(INVENTORY_NAME)) {
            event.setResult(Result.DENY);
            event.setCancelled(true);

            ItemStack clicked = event.getCurrentItem();

            if (clicked == null || clicked.getType() == Material.AIR) {
                return;
            }

            if (clicked.hasItemMeta() && clicked.getItemMeta().hasDisplayName()) {
                String name = clicked.getItemMeta().getDisplayName();
                boolean unspy = name.contains("Stop spying on");
                if (unspy) {
                    String channelName = name.replace("Stop spying on", "").trim();
                    chatPlayer.unspy(channelName);
                    player.sendMessage(Constants.COLOR_BASE + "No longer spying on " + Constants.COLOR_ACCENT + channelName);

                    ItemMeta itemMeta = clicked.getItemMeta();
                    itemMeta.setDisplayName("Spy on " + channelName);
                    clicked.setItemMeta(itemMeta);
                } else {
                    String channelName = name.replace("Spy on", "").trim();
                    chatPlayer.spy(channelName);
                    player.sendMessage(Constants.COLOR_BASE + "Now spying on " + Constants.COLOR_ACCENT + channelName);

                    ItemMeta itemMeta = clicked.getItemMeta();
                    itemMeta.setDisplayName("Stop spying on " + channelName);
                    clicked.setItemMeta(itemMeta);
                }
            }
        }
    }

}
