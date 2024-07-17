package net.sktemu.rms;

import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RmsManager implements AutoCloseable {
    private Connection sqlConn;

    public void initialize(File dataDir) throws RecordStoreException {
        File dbPath = new File(dataDir, "rms.db");
        String url = "jdbc:sqlite:" + dbPath;
        try {
            sqlConn = DriverManager.getConnection(url);
            sqlConn.setAutoCommit(false);

            initializeDBSchema();
        } catch (SQLException e) {
            close();
            throw new RecordStoreException("failed to connect SQL driver", e);
        }
    }

    @Override
    public void close() throws RecordStoreException {
        try {
            sqlConn.close();
            sqlConn = null;
        } catch (SQLException e) {
            throw new RecordStoreException("failed to close SQL connection", e);
        }
    }

    private void initializeDBSchema() throws SQLException {
        try (Statement stmt = sqlConn.createStatement()) {
            final String q = "CREATE TABLE IF NOT EXISTS rms_stores ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name VARCHAR(32) NOT NULL,"
                    + "next_record INTEGER NOT NULL DEFAULT 0,"
                    + "UNIQUE(name));";
            stmt.execute(q);

            sqlConn.commit();
        } catch (SQLException e) {
            sqlConn.rollback();
            throw e;
        }
    }

    public int getRecordStoreID(String name, boolean createIfNeccessary) throws RecordStoreException {
        int id;
        try {
            if (createIfNeccessary) {
                try (PreparedStatement stmt = sqlConn.prepareStatement("INSERT OR IGNORE INTO rms_stores(name) VALUES (?);")) {
                    stmt.setString(1, name);
                    stmt.execute();
                }
            }

            try (PreparedStatement stmt = sqlConn.prepareStatement("SELECT id FROM rms_stores WHERE name = ?;")) {
                stmt.setString(1, name);
                ResultSet res = stmt.executeQuery();
                if (!res.first()) {
                    throw new RecordStoreNotFoundException("no record store named " + name);
                }
                id = res.getInt(1);
            }

            sqlConn.commit();

            return id;
        } catch (SQLException e) {
            try {
                sqlConn.rollback();
            } catch (SQLException e1) {
                // ignore
            }
            throw new RecordStoreException("sql error occurred", e);
        }
    }

    public String[] listRecordStores() throws RecordStoreException {
        try (Statement stmt = sqlConn.createStatement()) {
            ResultSet res = stmt.executeQuery("SELECT name FROM rms_stores;");

            List<String> names = new ArrayList<>();

            while (res.next()) {
                names.add(res.getString(1));
            }

            return names.toArray(new String[0]);
        } catch (SQLException e) {
            throw new RecordStoreException("sql error occurred", e);
        }
    }

    public void deleteRecordStore(String recordStoreName) throws RecordStoreException {
        try (PreparedStatement stmt = sqlConn.prepareStatement("DELETE FROM rms_stores WHERE name = ?;")) {
            stmt.setString(1, recordStoreName);
            stmt.execute();
            sqlConn.commit();
        } catch (SQLException e) {
            try {
                sqlConn.rollback();
            } catch (SQLException e1) {
                // ignore
            }
            throw new RecordStoreException("sql error occurred", e);
        }
    }
}
