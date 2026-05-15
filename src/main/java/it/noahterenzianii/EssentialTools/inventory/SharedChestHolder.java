package it.noahterenzianii.EssentialTools.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Nullable;

public class SharedChestHolder implements InventoryHolder {

    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @Nullable Inventory getInventory() {
        return inventory;
    }
}
