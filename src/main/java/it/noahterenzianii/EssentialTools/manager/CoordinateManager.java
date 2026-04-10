package it.noahterenzianii.EssentialTools.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.model.SavedLocation;
import org.bukkit.Location;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CoordinateManager {
    private static final String DATA_FILE_NAME = "coordinate.json";

    private final Main plugin;
    private final Gson gson;
    private final java.io.File dataFile;
    private Map<String, SavedLocation> coordinates;

    public CoordinateManager(Main plugin) {
        this.plugin = plugin;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataFile = new java.io.File(plugin.getDataFolder(), DATA_FILE_NAME);
        this.coordinates = new HashMap<>();
        loadCoordinates();
    }

    public boolean saveCoordinate(String name, Location location, java.util.UUID uuid) {
        if (coordinates.containsKey(name)) {
            return false;
        }

        SavedLocation savedLocation = new SavedLocation(name, location, uuid);
        coordinates.put(name, savedLocation);
        saveToFile();
        return true;
    }

    public SavedLocation getCoordinate(String name) {
        for (Map.Entry<String, SavedLocation> entry : coordinates.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public boolean deleteCoordinate(String name) {
        for (Map.Entry<String, SavedLocation> entry : coordinates.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey())) {
                coordinates.remove(entry.getKey());
                saveToFile();
                return true;
            }
        }
        return false;
    }

    public Map<String, SavedLocation> getAllCoordinates() {
        return new TreeMap<>(coordinates);
    }

    private void loadCoordinates() {
        if (!dataFile.exists()) {
            plugin.getLogger().info("Location file not found, creating new file...");
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<HashMap<String, SavedLocation>>(){}.getType();
            Map<String, SavedLocation> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                coordinates = loaded;
            }
            plugin.getLogger().info("Loaded " + coordinates.size() + " locations");
        } catch (IOException e) {
            plugin.getLogger().severe("Error loading coordinates: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (FileWriter writer = new FileWriter(dataFile)) {
            gson.toJson(coordinates, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("Error saving coordinates: " + e.getMessage());
        }
    }
}