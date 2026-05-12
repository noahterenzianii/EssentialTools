package it.noahterenzianii.EssentialTools;

import it.noahterenzianii.EssentialTools.command.CoordinateCommand;
import it.noahterenzianii.EssentialTools.command.PingCommand;
import it.noahterenzianii.EssentialTools.command.SharedChestCommand;
import it.noahterenzianii.EssentialTools.listener.SharedChestListener;
import it.noahterenzianii.EssentialTools.manager.CoordinateManager;
import it.noahterenzianii.EssentialTools.manager.SharedChestManager;
import it.noahterenzianii.EssentialTools.manager.TooltipManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private CoordinateManager coordinateManager;
    private SharedChestManager sharedChestManager;

    @Override
    public void onEnable() {
        TooltipManager tooltipManager = new TooltipManager(this);
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        this.coordinateManager = new CoordinateManager(this);
        this.sharedChestManager = new SharedChestManager(this);

        getCommand("coord").setExecutor(new CoordinateCommand(this));
        getCommand("ping").setExecutor(new PingCommand(this));
        getCommand("sharedchest").setExecutor(new SharedChestCommand(this));

        Bukkit.getPluginManager().registerEvents(new SharedChestListener(this), this);

        Bukkit.getScheduler().runTaskTimer(
                this,
                tooltipManager::showCoords,
                0L,
                5L
        );

        getLogger().info("✅ EssentialTools Plugin loaded!");
    }

    @Override
    public void onDisable() {
        coordinateManager.close();
        getLogger().info("Plugin disabled.");
    }

    public CoordinateManager getCoordinateManager() {
        return coordinateManager;
    }

    public SharedChestManager getSharedChestManager() {
        return sharedChestManager;
    }

    public it.noahterenzianii.EssentialTools.database.DatabaseManager getDatabaseManager() {
        return coordinateManager.getDatabaseManager();
    }
}
