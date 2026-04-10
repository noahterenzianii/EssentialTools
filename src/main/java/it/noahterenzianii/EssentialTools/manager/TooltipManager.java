package it.noahterenzianii.EssentialTools.manager;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.EnumMap;
import java.util.Map;

public class TooltipManager {
    private static final String COORD_KEY = "coordActive";
    private static final int MAX_SCOREBOARD_ENTRIES = 10;

    private final it.noahterenzianii.EssentialTools.Main plugin;
    private final Map<World.Environment, ChatColor> worldColors;

    public TooltipManager(it.noahterenzianii.EssentialTools.Main plugin) {
        this.plugin = plugin;
        this.worldColors = new EnumMap<>(World.Environment.class);
        this.worldColors.put(World.Environment.NORMAL, ChatColor.DARK_GREEN);
        this.worldColors.put(World.Environment.NETHER, ChatColor.DARK_RED);
        this.worldColors.put(World.Environment.THE_END, ChatColor.DARK_PURPLE);
    }

    public void showCoords() {
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            NamespacedKey key = new NamespacedKey(plugin, COORD_KEY);
            boolean shouldShowScoreboard = Boolean.TRUE.equals(
                viewer.getPersistentDataContainer().get(key, PersistentDataType.BOOLEAN)
            );

            Scoreboard scoreboard = shouldShowScoreboard
                ? createScoreboard(viewer)
                : Bukkit.getScoreboardManager().getNewScoreboard();

            viewer.setScoreboard(scoreboard);
        }
    }

    private Scoreboard createScoreboard(Player viewer) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();

        Objective objective = board.registerNewObjective(
            "sidebar",
            Criteria.DUMMY,
            Component.text(ChatColor.GRAY + "Player Coordinates")
        );
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = 0;
        for (Player target : Bukkit.getOnlinePlayers()) {
            if (viewer.equals(target)) continue;
            if (score >= MAX_SCOREBOARD_ENTRIES) break;

            ChatColor worldColor = worldColors.getOrDefault(target.getWorld().getEnvironment(), ChatColor.WHITE);
            String line = String.format(
                "%s%s: %s%d %d %d%s",
                ChatColor.WHITE,
                target.getName(),
                worldColor,
                target.getLocation().getBlockX(),
                target.getLocation().getBlockY(),
                target.getLocation().getBlockZ(),
                getColorCode(score)
            );

            objective.getScore(line).setScore(++score);
        }

        return board;
    }

    private String getColorCode(int index) {
        return switch (index) {
            case 0 -> "§0";
            case 1 -> "§1";
            case 2 -> "§2";
            case 3 -> "§3";
            case 4 -> "§4";
            case 5 -> "§5";
            case 6 -> "§6";
            case 7 -> "§7";
            case 8 -> "§8";
            case 9 -> "§9";
            default -> "";
        };
    }
}