package it.noahterenzianii.EssentialTools.manager;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.model.SavedLocation;
import org.bukkit.Location;

import java.util.Map;
import java.util.TreeMap;

public class CoordinateManager {
    private final Main plugin;
    private final it.noahterenzianii.EssentialTools.database.DatabaseManager db;

    public CoordinateManager(Main plugin) {
        this.plugin = plugin;
        this.db = new it.noahterenzianii.EssentialTools.database.DatabaseManager(plugin);
    }

    public boolean saveCoordinate(String name, Location location, java.util.UUID uuid) {
        SavedLocation savedLocation = new SavedLocation(name, location, uuid);
        return db.saveCoordinate(name, savedLocation);
    }

    public SavedLocation getCoordinate(String name) {
        return db.getCoordinate(name);
    }

    public boolean deleteCoordinate(String name) {
        return db.deleteCoordinate(name);
    }

    public Map<String, SavedLocation> getAllCoordinates() {
        return new TreeMap<>(db.getAllCoordinates());
    }

    public void close() {
        db.close();
    }
}
