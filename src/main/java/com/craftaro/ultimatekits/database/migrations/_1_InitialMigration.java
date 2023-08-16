package com.craftaro.ultimatekits.database.migrations;

import com.craftaro.core.database.DataMigration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class _1_InitialMigration extends DataMigration {
    public _1_InitialMigration() {
        super(1);
    }

    @Override
    public void migrate(Connection connection, String tablePrefix) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE " + tablePrefix + "blockdata (" +
                    "type TEXT NOT NULL," +
                    "kit TEXT NOT NULL," +
                    "holograms BOOLEAN NOT NULL," +
                    "displayItems BOOLEAN NOT NULL," +
                    "particles BOOLEAN NOT NULL," +
                    "itemOverride BOOLEAN NOT NULL," +
                    "world TEXT NOT NULL," + // PK
                    "x INTEGER NOT NULL," + // PK
                    "y INTEGER NOT NULL," + // PK
                    "z INTEGER NOT NULL " + // PK
                    ")");
        }
    }
}
