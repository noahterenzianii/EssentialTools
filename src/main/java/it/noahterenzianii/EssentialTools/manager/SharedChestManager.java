package it.noahterenzianii.EssentialTools.manager;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.inventory.SharedChestHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedChestManager {

    private static final int CHEST_SIZE = 54;
    private static final String CHEST_TITLE = "Shared Chest";

    private final Main plugin;
    private final Set<UUID> activeViewers = new HashSet<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Inventory sharedInventory;
    private boolean loaded = false;

    public SharedChestManager(Main plugin) {
        this.plugin = plugin;
    }

    public Inventory openChest(Player player) {
        lock.writeLock().lock();
        try {
            if (!loaded) {
                SharedChestHolder holder = new SharedChestHolder();
                sharedInventory = Bukkit.createInventory(holder, CHEST_SIZE, CHEST_TITLE);
                holder.setInventory(sharedInventory);

                Map<Integer, ItemStack> items = plugin.getDatabaseManager().loadAllSharedChestItems();
                for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                    sharedInventory.setItem(entry.getKey(), entry.getValue().clone());
                }

                loaded = true;
            }

            activeViewers.add(player.getUniqueId());
            return sharedInventory;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void removeViewer(Player player) {
        lock.writeLock().lock();
        try {
            activeViewers.remove(player.getUniqueId());
            if (activeViewers.isEmpty() && loaded && sharedInventory != null) {
                plugin.getDatabaseManager().saveAllSharedChestItems(inventoryToMap(sharedInventory));
                sharedInventory.clear();
                sharedInventory = null;
                loaded = false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void saveToDatabase() {
        lock.readLock().lock();
        try {
            if (!loaded || sharedInventory == null) return;
            plugin.getDatabaseManager().saveAllSharedChestItems(inventoryToMap(sharedInventory));
        } finally {
            lock.readLock().unlock();
        }
    }

    private Map<Integer, ItemStack> inventoryToMap(Inventory inventory) {
        Map<Integer, ItemStack> items = new java.util.HashMap<>();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ItemStack item = inventory.getItem(slot);
            if (item != null && !item.isEmpty()) {
                items.put(slot, item.clone());
            }
        }
        return items;
    }
}
