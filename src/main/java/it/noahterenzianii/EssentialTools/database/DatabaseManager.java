package it.noahterenzianii.EssentialTools.database;

import it.noahterenzianii.EssentialTools.Main;
import it.noahterenzianii.EssentialTools.model.SavedLocation;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DatabaseManager {
    private final Main plugin;
    private Connection connection;

    public DatabaseManager(Main plugin) {
        this.plugin = plugin;
        initializeConnection();
        createTables();
    }

    private void initializeConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            String dbPath = plugin.getDataFolder() + "/coordinates.db";
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            connection.setAutoCommit(false);
            plugin.getLogger().info("Database connection established");
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void createTables() {
        String sql = """
            CREATE TABLE IF NOT EXISTS coordinates (
                name TEXT PRIMARY KEY,
                world TEXT NOT NULL,
                uuid TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL
            )
            """;
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            connection.commit();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to create tables: " + e.getMessage());
        }
    }

    public boolean saveCoordinate(String name, SavedLocation location) {
        String sql = "INSERT INTO coordinates (name, world, uuid, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, location.getName());
            pstmt.setString(2, location.getWorld());
            pstmt.setString(3, location.getPlayerID().toString());
            pstmt.setDouble(4, location.getX());
            pstmt.setDouble(5, location.getY());
            pstmt.setDouble(6, location.getZ());
            pstmt.executeUpdate();
            connection.commit();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                plugin.getLogger().severe("Failed to rollback: " + rollbackEx.getMessage());
            }
            return false;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error saving coordinate: " + e.getMessage());
            return false;
        }
    }

    public SavedLocation getCoordinate(String name) {
        String sql = "SELECT * FROM coordinates WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new SavedLocation(
                    rs.getString("name"),
                    rs.getString("world"),
                    java.util.UUID.fromString(rs.getString("uuid")),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting coordinate: " + e.getMessage());
        }
        return null;
    }

    public boolean deleteCoordinate(String name) {
        String sql = "DELETE FROM coordinates WHERE LOWER(name) = LOWER(?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            int affected = pstmt.executeUpdate();
            connection.commit();
            return affected > 0;
        } catch (SQLException e) {
            plugin.getLogger().severe("Error deleting coordinate: " + e.getMessage());
            return false;
        }
    }

    public Map<String, SavedLocation> getAllCoordinates() {
        Map<String, SavedLocation> coordinates = new HashMap<>();
        String sql = "SELECT * FROM coordinates ORDER BY name";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                SavedLocation loc = new SavedLocation(
                    rs.getString("name"),
                    rs.getString("world"),
                    java.util.UUID.fromString(rs.getString("uuid")),
                    rs.getDouble("x"),
                    rs.getDouble("y"),
                    rs.getDouble("z")
                );
                coordinates.put(rs.getString("name"), loc);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting all coordinates: " + e.getMessage());
        }
        return coordinates;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Database connection closed");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database: " + e.getMessage());
        }
    }
}
