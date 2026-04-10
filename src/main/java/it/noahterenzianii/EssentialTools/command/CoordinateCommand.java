package it.noahterenzianii.EssentialTools.command;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.manager.CoordinateManager;
import it.noahterenzianii.EssentialTools.manager.TooltipManager;
import it.noahterenzianii.EssentialTools.model.SavedLocation;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import java.util.Map;

public class CoordinateCommand implements CommandExecutor {
    private static final String COORD_KEY = "coordActive";

    private final Main plugin;

    public CoordinateCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmdName = command.getName().toLowerCase();

        return switch (cmdName) {
            case "coord" -> handleCoordCommand(sender, args);
            default -> false;
        };
    }

    private boolean handleCoordCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return handleTooltipCommand(sender);
        }

        String subcommand = args[0].toLowerCase();
        String[] subArgs = args.length > 1 ? java.util.Arrays.copyOfRange(args, 1, args.length) : new String[0];

        return switch (subcommand) {
            case "tooltip", "t" -> handleTooltipCommand(sender);
            case "get", "g" -> handleGetCoordCommand(sender, subArgs);
            case "set", "s" -> handleSetCoordCommand(sender, subArgs);
            case "del", "d", "remove", "r" -> handleDelCoordCommand(sender, subArgs);
            case "all", "a" -> handleGetAllCoordsCommand(sender);
            default -> {
                sender.sendMessage(ChatColor.RED + "Usage: /coord <get_set_del_all_tooltip> [arguments]");
                yield true;
            }
        };
    }

    private boolean handleTooltipCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        NamespacedKey key = new NamespacedKey(plugin, COORD_KEY);
        boolean shouldShow = !Boolean.TRUE.equals(
            player.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN)
        );
        player.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, shouldShow);
        new TooltipManager(plugin).showCoords();
        return true;
    }

    private boolean handleSetCoordCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /coord set <name>");
            return true;
        }

        String name = args[0];
        boolean success = plugin.getCoordinateManager().saveCoordinate(name, player.getLocation(), player.getUniqueId());

        if (success) {
            player.sendMessage(ChatColor.GREEN + "Location saved as: " + name);
        } else {
            player.sendMessage(ChatColor.RED + "Name already exists! Choose a different name.");
        }
        return true;
    }

    private boolean handleGetCoordCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /coord get <name>");
            return true;
        }

        String name = args[0];
        SavedLocation saved = plugin.getCoordinateManager().getCoordinate(name);

        if (saved == null) {
            sender.sendMessage(ChatColor.RED + "Location not found: " + name);
        } else {
            sender.sendMessage(ChatColor.GOLD + "Location '" + name + "':");
            sender.sendMessage(ChatColor.YELLOW + "World: " + saved.getWorld());
            sender.sendMessage(ChatColor.YELLOW + "X: " + String.format("%.1f", saved.getX()));
            sender.sendMessage(ChatColor.YELLOW + "Y: " + String.format("%.1f", saved.getY()));
            sender.sendMessage(ChatColor.YELLOW + "Z: " + String.format("%.1f", saved.getZ()));
        }
        return true;
    }

    private boolean handleDelCoordCommand(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "Usage: /coord del <name>");
            return true;
        }

        String name = args[0];
        boolean success = plugin.getCoordinateManager().deleteCoordinate(name);

        if (success) {
            sender.sendMessage(ChatColor.GREEN + "Location deleted: " + name);
        } else {
            sender.sendMessage(ChatColor.RED + "Location not found: " + name);
        }
        return true;
    }

    private boolean handleGetAllCoordsCommand(CommandSender sender) {
        Map<String, SavedLocation> allCoords = plugin.getCoordinateManager().getAllCoordinates();

        if (allCoords.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No saved locations.");
        } else {
            sender.sendMessage(ChatColor.GOLD + "=== SAVED LOCATIONS ===");
            for (Map.Entry<String, SavedLocation> entry : allCoords.entrySet()) {
                SavedLocation coord = entry.getValue();
                String name = entry.getKey();
                Component message = Component.text()
                        .append(Component.text(name, NamedTextColor.GREEN))
                        .append(Component.text(" - ", NamedTextColor.WHITE))
                        .append(Component.text(
                                String.format("%s %.0f %.0f %.0f",
                                        coord.getWorld(), coord.getX(), coord.getY(), coord.getZ()),
                                NamedTextColor.WHITE))
                        .clickEvent(ClickEvent.runCommand("/ping " + name))
                        .hoverEvent(HoverEvent.showText(Component.text("Click to ping", NamedTextColor.GRAY)))
                        .build();
                sender.sendMessage(message);
            }
        }
        return true;
    }
}