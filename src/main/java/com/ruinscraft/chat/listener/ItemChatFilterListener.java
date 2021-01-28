package com.ruinscraft.chat.listener;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.util.FilterUtil;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class ItemChatFilterListener implements Listener {

    private ChatPlugin chatPlugin;

    public ItemChatFilterListener(ChatPlugin chatPlugin) {
        this.chatPlugin = chatPlugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack itemStack = event.getItem();
        handleItem(itemStack);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        ItemStack itemStack = event.getItem();
        handleItem(itemStack);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        ItemStack itemStack = event.getItem().getItemStack();
        handleItem(itemStack);
    }

    @EventHandler
    public void onAnvil(PrepareAnvilEvent event) {
        ItemStack itemStack = event.getResult();
        handleItem(itemStack);
    }

    private void handleItem(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if (!itemStack.hasItemMeta()) {
            return;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta instanceof BookMeta) {
            BookMeta bookMeta = (BookMeta) itemMeta;

            boolean clear = false;

            for (String page : bookMeta.getPages()) {
                if (FilterUtil.isBadMessage(chatPlugin.getRacialSlurs(), page)) {
                    clear = true;
                }
            }

            if (clear) {
                bookMeta.setPages(new ArrayList<>());
            }
        }

        if (itemMeta.hasDisplayName()) {
            boolean nameIsBad = FilterUtil.isBadMessage(chatPlugin.getRacialSlurs(), itemMeta.getDisplayName());

            if (nameIsBad) {
                itemMeta.setDisplayName(null);
            }
        }

        if (itemMeta.hasLore()) {
            boolean loreIsBad = false;

            for (String line : itemMeta.getLore()) {
                boolean lineIsBad = FilterUtil.isBadMessage(chatPlugin.getRacialSlurs(), line);

                if (lineIsBad) {
                    loreIsBad = true;
                }
            }

            if (loreIsBad) {
                itemMeta.setLore(null);
            }
        }

        itemStack.setItemMeta(itemMeta);
    }

}
