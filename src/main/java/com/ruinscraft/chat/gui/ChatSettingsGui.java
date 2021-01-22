package com.ruinscraft.chat.gui;

import com.ruinscraft.chat.ChatPlugin;
import com.ruinscraft.chat.player.OnlineChatPlayer;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ChatSettingsGui extends Gui {

    private static final String ENABLE_FILTER = "Enable chat filter";
    private static final String DISABLE_FILTER = "Disable chat filter";
    private static final String ALLOW_DMS_FROM_ANYONE = "Allow direct messages from anyone";
    private static final String ALLOW_DMS_FROM_FRIENDS = "Only allow direct messages from friends";
    private ChatPlugin chatPlugin;

    public ChatSettingsGui(String name, int size, ChatPlugin chatPlugin) {
        super(name, size);
        this.chatPlugin = chatPlugin;
    }

    @Override
    public void onGuiClick(InventoryClickEvent event) {
        HumanEntity user = event.getWhoClicked();
        ItemStack item = event.getCurrentItem();

        if (!(user instanceof Player)) {
            return;
        }

        Player player = (Player) user;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        ItemMeta itemMeta = item.getItemMeta();
        String itemName = itemMeta.getDisplayName();

        switch (itemName) {
            case ENABLE_FILTER:
                onlineChatPlayer.getPersonalizationSettings().setHideProfanity(true);
                break;
            case DISABLE_FILTER:
                onlineChatPlayer.getPersonalizationSettings().setHideProfanity(false);
                break;
            case ALLOW_DMS_FROM_ANYONE:
                onlineChatPlayer.getPersonalizationSettings().setAllowDmsFromAnyone(true);
                break;
            case ALLOW_DMS_FROM_FRIENDS:
                onlineChatPlayer.getPersonalizationSettings().setAllowDmsFromAnyone(false);
                break;
            default:
                return;
        }

        chatPlugin.getChatStorage().savePersonalizationSettings(onlineChatPlayer, onlineChatPlayer.getPersonalizationSettings());
    }

    @Override
    public void populateInventory(HumanEntity user, Inventory inventory) {
        if (!(user instanceof Player)) {
            return;
        }

        Player player = (Player) user;
        OnlineChatPlayer onlineChatPlayer = chatPlugin.getChatPlayerManager().get(player);

        final ItemStack profanityItemStack;
        if (onlineChatPlayer.getPersonalizationSettings().isHideProfanity()) {
            profanityItemStack = new ItemStack(Material.MUSIC_DISC_CAT, 1);
            ItemMeta itemMeta = profanityItemStack.getItemMeta();
            itemMeta.setDisplayName(DISABLE_FILTER);
            profanityItemStack.setItemMeta(itemMeta);
        } else {
            profanityItemStack = new ItemStack(Material.MUSIC_DISC_BLOCKS, 1);
            ItemMeta itemMeta = profanityItemStack.getItemMeta();
            itemMeta.setDisplayName(ENABLE_FILTER);
            profanityItemStack.setItemMeta(itemMeta);
        }
        inventory.setItem(0, profanityItemStack);

        final ItemStack dmsItemStack;
        if (onlineChatPlayer.getPersonalizationSettings().isAllowDmsFromAnyone()) {
            dmsItemStack = new ItemStack(Material.MUSIC_DISC_CAT, 1);
            ItemMeta itemMeta = dmsItemStack.getItemMeta();
            itemMeta.setDisplayName(ALLOW_DMS_FROM_FRIENDS);
            dmsItemStack.setItemMeta(itemMeta);
        } else {
            dmsItemStack = new ItemStack(Material.MUSIC_DISC_BLOCKS, 1);
            ItemMeta itemMeta = dmsItemStack.getItemMeta();
            itemMeta.setDisplayName(ALLOW_DMS_FROM_ANYONE);
            dmsItemStack.setItemMeta(itemMeta);
        }
        inventory.setItem(1, dmsItemStack);
    }

}
