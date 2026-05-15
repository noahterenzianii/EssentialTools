package it.noahterenzianii.EssentialTools.command;

import it.noahterenzianii.EssentialTools.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SharedChestCommand implements CommandExecutor {

    private final Main plugin;

    public SharedChestCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        Inventory chest = plugin.getSharedChestManager().openChest(player);
        player.openInventory(chest);
        return true;
    }
}
