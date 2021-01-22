package com.ruinscraft.chat.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public abstract class Gui implements Listener {

    private String name;
    private int size;
    private Map<HumanEntity, Inventory> currentUsers;

    public Gui(String name, int size) {
        this.name = name;
        this.size = size;
        currentUsers = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public Map<HumanEntity, Inventory> getCurrentUsers() {
        return currentUsers;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.getServer().createInventory(null, size, name);
        populateInventory(player, inventory);
        currentUsers.put(player, inventory);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        HumanEntity user = event.getPlayer();
        Inventory closed = event.getInventory();
        Inventory using = currentUsers.get(user);

        if (closed.equals(using)) {
            currentUsers.remove(user);
        }
    }

    @EventHandler
    public void onInventoryClick0(InventoryClickEvent event) {
        HumanEntity user = event.getWhoClicked();
        Inventory current = event.getInventory();
        Inventory using = currentUsers.get(user);

        if (current.equals(using)) {
            if (event.getCurrentItem() == null
                    || event.getCurrentItem().getType() == Material.AIR) {
                return;
            }

            event.setCancelled(true);
            onGuiClick(event);

            for (HumanEntity currentUser : getCurrentUsers().keySet()) {
                Inventory inventory = getCurrentUsers().get(currentUser);
                inventory.clear();
                populateInventory(currentUser, inventory);
            }
        }
    }

    public abstract void onGuiClick(InventoryClickEvent event);

    public abstract void populateInventory(HumanEntity user, Inventory inventory);

}