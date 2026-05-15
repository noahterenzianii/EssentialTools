package it.noahterenzianii.EssentialTools.listener;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.inventory.SharedChestHolder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class SharedChestListener implements Listener {

    private final Main plugin;

    public SharedChestListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder() instanceof SharedChestHolder)) return;
        if (!(event.getPlayer() instanceof Player player)) return;

        plugin.getSharedChestManager().removeViewer(player);
    }
}
