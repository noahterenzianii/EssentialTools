package it.noahterenzianii.EssentialTools.manager;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.inventory.SharedChestHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class SharedChestManager {

    private static final int CHEST_SIZE = 54;
    private static final String CHEST_TITLE = "Shared Chest";

    private final Main plugin;

    public SharedChestManager(Main plugin) {
        this.plugin = plugin;
    }

    public Inventory getChestInventory(Player player) {
        SharedChestHolder holder = new SharedChestHolder();
        Inventory inv = Bukkit.createInventory(holder, CHEST_SIZE, CHEST_TITLE);

        Map<Integer, ItemStack> items = plugin.getDatabaseManager().loadAllSharedChestItems();
        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
            inv.setItem(entry.getKey(), entry.getValue());
        }

        return inv;
    }

    public void saveChestInventory(Inventory inventory) {
        plugin.getDatabaseManager().clearSharedChestItems();

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && !item.isEmpty()) {
                plugin.getDatabaseManager().saveSharedChestItem(i, item);
            }
        }
    }
}
