package it.noahterenzianii.EssentialTools.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class SavedLocation {
    private String name;
    private String world;
    private UUID playerID;
    private double x;
    private double y;
    private double z;

    public SavedLocation() {
    }

    public SavedLocation(String name, Location location, UUID uuid) {
        this.name = name;
        this.world = location.getWorld().getName();
        this.playerID = uuid;
        this.x = Math.round(location.getX());
        this.y = Math.round(location.getY());
        this.z = Math.round(location.getZ());
    }

    public SavedLocation(String name, String world, UUID uuid, double x, double y, double z) {
        this.name = name;
        this.world = world;
        this.playerID = uuid;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location toLocation() {
        World w = Bukkit.getWorld(world);
        if (w == null) return null;
        return new Location(w, x, y, z);
    }

    public Location toLocation(World world) {
        return new Location(world, x, y, z);
    }

    public String getName() {
        return name;
    }

    public String getWorld() {
        return world;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}