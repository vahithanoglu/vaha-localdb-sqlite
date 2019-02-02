package com.vahabilisim.localdb.sqlite;

import com.vahabilisim.localdb.LocalDBCursor;
import com.vahabilisim.localdb.LocalDBTrans;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.apache.log4j.Logger;

public class SQLiteDBTrans implements LocalDBTrans {

    private static final Logger LOGGER = Logger.getLogger("vahabilisim.localdb-sqlite");

    private final Connection conn;
    private final int timeoutInSec;

    private boolean error;

    public SQLiteDBTrans(Connection conn, int timeoutInSec) {
        this.conn = conn;
        this.timeoutInSec = timeoutInSec;
        error = false;
    }

    private ResultSet executeQuery(String query, Object[] params) {
        if (error) {
            return null;
        }
        try {
            final PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setQueryTimeout(timeoutInSec);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            return stmt.executeQuery();
        } catch (SQLException ex) {
            LOGGER.error("Cannot execute query " + query, ex);
            error = true;
            return null;
        }
    }

    private int executeStatement(String statement, Object[] params) {
        if (error) {
            return 0;
        }
        try {
            final PreparedStatement stmt = conn.prepareStatement(statement);
            stmt.setQueryTimeout(timeoutInSec);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
            }
            stmt.execute();
            return stmt.getUpdateCount();
        } catch (SQLException ex) {
            LOGGER.error("Cannot execute statement " + statement, ex);
            error = true;
            return 0;
        }
    }

    @Override
    public synchronized boolean success() {
        return false == error;
    }

    @Override
    public synchronized void commit() {
        try {
            if (error == false) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (SQLException ex) {
            LOGGER.error("Cannot commit", ex);
            error = true;
        }
        try {
            conn.close();
        } catch (SQLException ex) {
        }
    }

    @Override
    public void rollback() {
        try {
            conn.rollback();
        } catch (SQLException ex) {
            LOGGER.error("Cannot rollback", ex);
            error = true;
        }
        try {
            conn.close();
        } catch (SQLException ex) {
        }
    }

    @Override
    public synchronized void execSQL(String sql) {
        executeStatement(sql, null);
    }

    @Override
    public synchronized void execSQL(String sql, Object[] bindArgs) {
        executeStatement(sql, bindArgs);
    }

    @Override
    public synchronized void insert(String table, String nullColumnHack, Map<String, Object> values) {
        final StringBuilder sb = new StringBuilder();
        final StringBuilder vals = new StringBuilder();
        final Object[] params = new Object[values.size()];

        int i = 0;
        for (final String key : values.keySet()) {
            if (i == 0) {
                sb.append("INSERT INTO ");
                sb.append(table);
                sb.append("(");
                sb.append(key);
                vals.append(" VALUES(?");
                params[i] = values.get(key);

            } else {
                sb.append(",");
                sb.append(key);
                vals.append(",?");
                params[i] = values.get(key);
            }
            i++;
        }
        sb.append(")");
        vals.append(")");
        sb.append(vals);

        executeStatement(sb.toString(), params);
    }

    @Override
    public synchronized int delete(String table, String whereClause, String[] whereArgs) {
        final StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(table);
        if (whereClause != null) {
            sb.append(" WHERE ");
            sb.append(whereClause);
        }
        return executeStatement(sb.toString(), whereArgs);
    }

    @Override
    public synchronized int update(String table, Map<String, Object> values, String whereClause, String[] whereArgs) {
        final StringBuilder sb = new StringBuilder();
        final Object[] params = new Object[values.size() + (whereArgs == null ? 0 : whereArgs.length)];

        int i = 0;
        for (final String key : values.keySet()) {
            if (i == 0) {
                sb.append("UPDATE ");
                sb.append(table);
                sb.append(" SET ");
                sb.append(key);
                sb.append("=?");
                params[i] = values.get(key);

            } else {
                sb.append(",");
                sb.append(key);
                sb.append("=?");
                params[i] = values.get(key);
            }
            i++;
        }

        if (whereArgs != null) {
            sb.append(" WHERE ");
            sb.append(whereClause);
            for (final String arg : whereArgs) {
                params[i] = arg;
                i++;
            }
        }

        return executeStatement(sb.toString(), params);
    }

    @Override
    public synchronized LocalDBCursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        final StringBuilder sb = new StringBuilder("SELECT");

        for (int i = 0; i < columns.length; i++) {
            if (i == 0) {
                sb.append(" ");
                sb.append(columns[i]);
            } else {
                sb.append(",");
                sb.append(columns[i]);
            }
        }

        sb.append(" FROM ");
        sb.append(table);

        if (selection != null) {
            sb.append(" WHERE ");
            sb.append(selection);
        }
        if (groupBy != null) {
            sb.append(" GROUP BY ");
            sb.append(groupBy);
        }
        if (having != null) {
            sb.append(" HAVING ");
            sb.append(having);
        }
        if (orderBy != null) {
            sb.append(" ORDER BY ");
            sb.append(orderBy);
        }
        if (limit != null) {
            sb.append(" LIMIT ");
            sb.append(limit);
        }

        return new SQLiteDBCursor(executeQuery(sb.toString(), selectionArgs));
    }
}
