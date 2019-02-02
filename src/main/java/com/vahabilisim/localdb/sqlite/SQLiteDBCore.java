package com.vahabilisim.localdb.sqlite;

import com.vahabilisim.localdb.LocalDBCore;
import com.vahabilisim.localdb.LocalDBCursor;
import com.vahabilisim.localdb.LocalDBException;
import com.vahabilisim.localdb.LocalDBTrans;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.sqlite.SQLiteConfig;

public abstract class SQLiteDBCore implements LocalDBCore {

    private static final String VERSION_NUMBER = "version_number";
    private static final String VERSION_TABLE = "localdb_sqlite_version";

    private final String dbName;
    private final int version;
    private final int timeoutInSec;

    protected SQLiteDBCore(String dbName, int version, int timeoutInSec) throws LocalDBException {
        this.dbName = String.format("jdbc:sqlite:%s", dbName);
        this.version = version;
        this.timeoutInSec = timeoutInSec;

        final LocalDBTrans trans = startWritableTrans();
        try {
            final Map<String, Object> values = new HashMap<>();
            values.put(VERSION_NUMBER, version);

            LocalDBCursor cursor = trans.query("sqlite_master", new String[]{"COUNT(1)"}, "type = ? AND name = ?", new String[]{"table", VERSION_TABLE}, null, null, null, null);
            final boolean exist = (cursor.moveToFirst() && cursor.getInt(0) > 0);

            if (false == exist) {
                onCreate(trans);
                trans.execSQL("CREATE TABLE " + VERSION_TABLE + " (" + VERSION_NUMBER + " TEXT)");
                trans.insert(VERSION_TABLE, null, values);
            }

            cursor = trans.query(VERSION_TABLE, new String[]{VERSION_NUMBER}, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                final int currVer = cursor.getInt(0);
                if (version > currVer) {
                    onUpgrade(trans, currVer, version);
                    trans.delete(VERSION_TABLE, null, null);
                    trans.insert(VERSION_TABLE, null, values);
                }
            }

        } finally {
            trans.commit();
        }

        if (false == trans.success()) {
            throw new LocalDBException("Cannot init database");
        }
    }

    private Connection createConnection(boolean readOnly) throws ClassNotFoundException, SQLException {
        boolean success = false;
        Connection conn = null;
        try {
            // Class.forName("org.sqlite.JDBC");

            final SQLiteConfig config = new SQLiteConfig();
            config.setReadOnly(readOnly);

            DriverManager.setLoginTimeout(timeoutInSec);
            conn = DriverManager.getConnection(dbName, config.toProperties());
            conn.setAutoCommit(false);
            conn.setReadOnly(readOnly);
            success = true;
            return conn;
        } finally {
            if (false == success && null != conn) {
                conn.close();
            }
        }
    }

    @Override
    public LocalDBTrans startReadableTrans() throws LocalDBException {
        try {
            return new SQLiteDBTrans(createConnection(true), timeoutInSec);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new LocalDBException("Cannot open readable connection", ex);
        }
    }

    @Override
    public LocalDBTrans startWritableTrans() throws LocalDBException {
        try {
            return new SQLiteDBTrans(createConnection(false), timeoutInSec);
        } catch (ClassNotFoundException | SQLException ex) {
            throw new LocalDBException("Cannot open readable connection", ex);
        }
    }

}
