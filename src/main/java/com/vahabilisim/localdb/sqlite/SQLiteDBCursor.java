package com.vahabilisim.localdb.sqlite;

import com.vahabilisim.localdb.LocalDBCursor;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;

public class SQLiteDBCursor implements LocalDBCursor {

    private static final Logger LOGGER = Logger.getLogger("vahabilisim.localdb-sqlite");
    private final ResultSet resultSet;

    public SQLiteDBCursor(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean moveToFirst() {
        try {
            return resultSet == null ? false : resultSet.next();
        } catch (SQLException ex) {
            LOGGER.error("Cannot move to first", ex);
            return false;
        }
    }

    @Override
    public boolean moveToNext() {
        try {
            return resultSet == null ? false : resultSet.next();
        } catch (SQLException ex) {
            LOGGER.error("Cannot move to next", ex);
            return false;
        }
    }

    @Override
    public byte[] getBlob(int columnIndex) {
        try {
            return resultSet.getBytes(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get blob", ex);
            return null;
        }
    }

    @Override
    public String getString(int columnIndex) {
        try {
            return resultSet.getString(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get string", ex);
            return null;
        }
    }

    @Override
    public short getShort(int columnIndex) {
        try {
            return resultSet.getShort(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get short", ex);
            return 0;
        }
    }

    @Override
    public int getInt(int columnIndex) {
        try {
            return resultSet.getInt(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get int", ex);
            return 0;
        }
    }

    @Override
    public long getLong(int columnIndex) {
        try {
            return resultSet.getLong(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get long", ex);
            return 0;
        }
    }

    @Override
    public float getFloat(int columnIndex) {
        try {
            return resultSet.getFloat(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get float", ex);
            return 0.0f;
        }
    }

    @Override
    public double getDouble(int columnIndex) {
        try {
            return resultSet.getDouble(columnIndex + 1);
        } catch (SQLException ex) {
            LOGGER.error("Cannot get double", ex);
            return 0.0d;
        }
    }
}
