package com.songoda.ultimatekits.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DataMigration {

    private final int revision;

    public DataMigration(int revision) {
        this.revision = revision;
    }

    public abstract void migrate(Connection connection, String tablePrefix) throws SQLException;

    public int getRevision() {
        return this.revision;
    }
}
