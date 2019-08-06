package com.songoda.ultimatekits.database;

import com.songoda.ultimatekits.UltimateKits;
import com.songoda.ultimatekits.kit.KitBlockData;
import com.songoda.ultimatekits.kit.KitType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DataManager {

    private final DatabaseConnector databaseConnector;
    private final Plugin plugin;

    public DataManager(DatabaseConnector databaseConnector, Plugin plugin) {
        this.databaseConnector = databaseConnector;
        this.plugin = plugin;
    }

    public String getTablePrefix() {
        return this.plugin.getDescription().getName().toLowerCase() + '_';
    }

    public void bulkUpdateBlockData(Map<Location, KitBlockData> blockData) {
        this.databaseConnector.connect(connection -> {
            String updateData = "UPDATE " + this.getTablePrefix() + "blockdata SET kit = ? WHERE " +
                    "world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateData)) {
                for (int i = 0; i < blockData.size(); i++) {
                    KitBlockData data = blockData.get(i);
                    statement.setString(2, data.getKit().getName());
                    statement.setString(7, data.getWorld().getName());
                    statement.setInt(8, data.getX());
                    statement.setInt(9, data.getY());
                    statement.setInt(10, data.getZ());
                    statement.executeUpdate();
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        });
    }

    public void updateBlockData(KitBlockData blockData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String updateData = "UPDATE " + this.getTablePrefix() + "blockdata SET kit = ? WHERE " +
                    "world = ? AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateData)) {
                statement.setString(2, blockData.getKit().getName());
                statement.setString(7, blockData.getWorld().getName());
                statement.setInt(8, blockData.getX());
                statement.setInt(9, blockData.getY());
                statement.setInt(10, blockData.getZ());
                statement.executeUpdate();
            }
        }));
    }

    public void createBlockData(KitBlockData blockData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String createData ="INSERT INTO " + this.getTablePrefix() + "blockdata (" +
                    "type, kit, holograms, displayItems, particles, itemOverride, world, x, y, z)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(createData)) {
                statement.setString(1, blockData.getType().toString());
                statement.setString(2, blockData.getKit().getName());
                statement.setBoolean(3, blockData.showHologram());
                statement.setBoolean(4, blockData.isDisplayingItems());
                statement.setBoolean(5, blockData.hasParticles());
                statement.setBoolean(6, blockData.isItemOverride());
                statement.setString(7, blockData.getWorld().getName());
                statement.setInt(8, blockData.getX());
                statement.setInt(9, blockData.getY());
                statement.setInt(10, blockData.getZ());
                statement.executeUpdate();
            }
        }));
    }

    public void deleteBlockData(KitBlockData blockData) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String deleteData = "DELETE FROM " + this.getTablePrefix() + "blockdata WHERE world = ? " +
                    "AND x = ? AND y = ? AND z = ?";
            try (PreparedStatement statement = connection.prepareStatement(deleteData)) {
                statement.setString(1, blockData.getType().toString());
                statement.setString(2, blockData.getKit().getName());
                statement.setBoolean(3, blockData.showHologram());
                statement.setBoolean(4, blockData.isDisplayingItems());
                statement.setBoolean(5, blockData.hasParticles());
                statement.setBoolean(6, blockData.isItemOverride());
                statement.setString(7, blockData.getWorld().getName());
                statement.setInt(8, blockData.getX());
                statement.setInt(9, blockData.getY());
                statement.setInt(10, blockData.getZ());
                statement.executeUpdate();
            }
        }));
    }

    public void getBlockData(Consumer<Map<Location, KitBlockData>> callback) {
        this.async(() -> this.databaseConnector.connect(connection -> {
            String selectData = "SELECT * FROM " + this.getTablePrefix() + "blockdata";
            Map<Location, KitBlockData> blockData = new HashMap<>();
            try (Statement statement = connection.createStatement()) {
                ResultSet result = statement.executeQuery(selectData);
                while (result.next()) {
                    KitType type = KitType.valueOf(result.getString("type"));
                    String kit = result.getString("kit");
                    boolean holograms = result.getBoolean("holograms");
                    boolean displayItems = result.getBoolean("displayItems");
                    boolean particles = result.getBoolean("particles");
                    boolean itemOverride = result.getBoolean("itemOverride");
                    World world = Bukkit.getWorld(result.getString("world"));
                    int x = result.getInt("x");
                    int y = result.getInt("y");
                    int z = result.getInt("z");
                    Location location = new Location(world, x, y, z);

                    KitBlockData kitBlockData =
                            new KitBlockData(UltimateKits.getInstance().getKitManager().getKit(kit),
                                    location, type, holograms, particles, displayItems, itemOverride);
                    blockData.put(location, kitBlockData);
                }
            }

            this.sync(() -> callback.accept(blockData));
        }));
    }

    private int lastInsertedId(Connection connection) {
        String query;
        if (this.databaseConnector instanceof SQLiteConnector) {
            query = "SELECT last_insert_rowid()";
        } else {
            query = "SELECT LAST_INSERT_ID()";
        }

        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(query);
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void async(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void sync(Runnable runnable) {
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }
}
