package com.songoda.ultimatekits.database.migrations;

import com.songoda.core.database.DataMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class _2_DuplicateMigration extends DataMigration {

    private final boolean sqlite;

    public _2_DuplicateMigration(boolean sqlite) {
        super(2);
        this.sqlite = sqlite;
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        // Fix duplicate data caused by old sqlite data duplication bug
        if (sqlite) {
            HashMap<String, TempKitData> data = new HashMap();
            // grab a copy of the unique data values
            try (Statement statement = connection.createStatement()) {
                ResultSet allData = statement.executeQuery("SELECT * FROM " + tablePrefix + "blockdata");
                while (allData.next()) {
                    String world = allData.getString("world");
                    int x = allData.getInt("x");
                    int y = allData.getInt("y");
                    int z = allData.getInt("z");
                    String key = world + ";" + x + ";" + y + ";" + z + ";";
                    if (!data.containsKey(key)) {
                        data.put(key, new TempKitData(
                                allData.getString("type"),
                                allData.getString("kit"),
                                allData.getBoolean("holograms"),
                                allData.getBoolean("displayItems"),
                                allData.getBoolean("particles"),
                                allData.getBoolean("itemOverride"),
                                world, x, y, z
                        ));
                    }
                }
                allData.close();
            }
            if (data.isEmpty()) return;
            connection.setAutoCommit(false);
            // first delete old data
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM " + tablePrefix + "blockdata");
            }
            // then re-add valid unique data
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tablePrefix + "blockdata (" +
                    "type, kit, holograms, displayItems, particles, itemOverride, world, x, y, z)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                for (TempKitData blockData : data.values()) {
                    statement.setString(1, blockData.type);
                    statement.setString(2, blockData.kit);
                    statement.setBoolean(3, blockData.holograms);
                    statement.setBoolean(4, blockData.displayItems);
                    statement.setBoolean(5, blockData.particles);
                    statement.setBoolean(6, blockData.itemOverride);
                    statement.setString(7, blockData.world);
                    statement.setInt(8, blockData.x);
                    statement.setInt(9, blockData.y);
                    statement.setInt(10, blockData.z);
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();
            connection.setAutoCommit(true);
            // free up disk space (sqlite command)
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("VACUUM");
            }
        }
    }

    private static class TempKitData {
        private final String type, kit, world;
        private final int x, y, z;
        private final boolean holograms, displayItems, particles, itemOverride;

        public TempKitData(String type, String kit, boolean holograms, boolean displayItems, boolean particles, boolean itemOverride, String world, int x, int y, int z) {
            this.type = type;
            this.kit = kit;
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.holograms = holograms;
            this.displayItems = displayItems;
            this.particles = particles;
            this.itemOverride = itemOverride;
        }
    }
}
