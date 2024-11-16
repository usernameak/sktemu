package net.sktemu.rms;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RmsManager implements AutoCloseable {
    private Connection sqlConn;

    private static final String SQL_INIT_1_SCHEMA =
            "CREATE TABLE IF NOT EXISTS rms_stores ("
                    + "    id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "    name VARCHAR(32) NOT NULL,"
                    + "    next_record INTEGER NOT NULL DEFAULT 1,"
                    + "    UNIQUE(name));";

    private static final String SQL_INIT_2_SCHEMA =
            "CREATE TABLE IF NOT EXISTS rms_records ("
                    + "    id INTEGER NOT NULL,"
                    + "    store_id INTEGER NOT NULL,"
                    + "    data BLOB NOT NULL,"
                    + "    PRIMARY KEY (id, store_id),"
                    + "    FOREIGN KEY (store_id) REFERENCES rms_stores(id) ON DELETE CASCADE"
                    + ");";

    private static final String SQL_GET_NEXT_RECORD_ID =
            "SELECT next_record FROM rms_stores WHERE id = ?;";

    private static final String SQL_INCREMENT_NEXT_RECORD =
            "UPDATE rms_stores SET next_record = next_record + 1 WHERE id = ?;";

    public void initialize(File dataDir) throws RecordStoreException {
        File dbPath = new File(dataDir, "rms.db");
        String url = "jdbc:sqlite:" + dbPath;
        try {
            sqlConn = DriverManager.getConnection(url);

            try (Statement stmt = sqlConn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }

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
            stmt.execute(SQL_INIT_1_SCHEMA);
            stmt.execute(SQL_INIT_2_SCHEMA);

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
                try (ResultSet res = stmt.executeQuery()) {
                    if (res.next()) {
                        id = res.getInt(1);
                    } else {
                        throw new RecordStoreNotFoundException("RecordStore " + name + " not found");
                    }
                }
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
            try (ResultSet res = stmt.executeQuery("SELECT name FROM rms_stores;")) {
                List<String> names = new ArrayList<>();

                while (res.next()) {
                    names.add(res.getString(1));
                }

                return names.toArray(new String[0]);
            }
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

    public byte[] getRecord(int recordStoreId, int recordId) throws RecordStoreException {
        try (PreparedStatement stmt = sqlConn.prepareStatement("SELECT data FROM rms_records WHERE id = ? AND store_id = ?;")) {
            stmt.setInt(1, recordId);
            stmt.setInt(2, recordStoreId);
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    return res.getBytes(1);
                } else {
                    throw new InvalidRecordIDException("Record " + recordId + " in RecordStore " + recordStoreId + " not found");
                }
            }
        } catch (SQLException e) {
            throw new RecordStoreException("sql error occurred", e);
        }
    }

    public int addRecord(int recordStoreId, byte[] data, int off, int len) throws RecordStoreException {
        int recordId;

        try (PreparedStatement stmt = sqlConn.prepareStatement(SQL_GET_NEXT_RECORD_ID)) {
            stmt.setInt(1, recordStoreId);
            try (ResultSet res = stmt.executeQuery()) {
                if (res.next()) {
                    recordId = res.getInt(1);
                    addRecordNoCommit(recordStoreId, recordId, data, off, len);

                    try (PreparedStatement updateStmt = sqlConn.prepareStatement(SQL_INCREMENT_NEXT_RECORD)) {
                        updateStmt.setInt(1, recordStoreId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    throw new RecordStoreNotFoundException("RecordStore with id " + recordStoreId + " not found");
                }
            }

            sqlConn.commit();
        } catch (SQLException e) {
            throw new RecordStoreException("sql error occurred", e);
        }

        return recordId;
    }

    public int deleteRecord(int recordStoreId, int recordId) throws RecordStoreException {
        // TODO:
        throw new RecordStoreException("not implemented yet");
    }

    private void addRecordNoCommit(int recordStoreId, int recordId, byte[] data, int off, int len) throws SQLException {
        try (PreparedStatement stmt = sqlConn.prepareStatement("INSERT INTO rms_records (id, store_id, data) VALUES (?, ?, ?);")) {
            stmt.setInt(1, recordId);
            stmt.setInt(2, recordStoreId);
            stmt.setBinaryStream(3, new ByteArrayInputStream(data, off, len), len);
            stmt.executeUpdate();
        }
    }
}
