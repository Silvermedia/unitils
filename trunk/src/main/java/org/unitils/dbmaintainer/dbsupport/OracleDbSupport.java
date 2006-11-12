package org.unitils.dbmaintainer.dbsupport;

import org.unitils.dbmaintainer.handler.StatementHandlerException;
import org.unitils.dbmaintainer.handler.StatementHandler;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.util.Set;
import java.util.HashSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 
 */
public class OracleDbSupport extends DbSupport {

    public OracleDbSupport() {
    }

    public Set<String> getSequenceNames() throws SQLException {
        return getDbItemsOfType("SEQUENCE_NAME", "USER_SEQUENCES");
    }

    public Set<String> getTriggerNames() throws SQLException {
        return getDbItemsOfType("TRIGGER_NAME", "USER_TRIGGERS");
    }

    public void dropView(String viewName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop view " + viewName + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }

    public void dropTable(String tableName) throws SQLException, StatementHandlerException {
        String dropTableSQL = "drop table " + tableName + " cascade constraints";
        statementHandler.handle(dropTableSQL);
    }

    public long getNextValueOfSequence(String sequenceName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select LAST_NUMBER from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'");
            rs.next();
            return rs.getLong("LAST_NUMBER");
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) throws StatementHandlerException, SQLException {
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select LAST_NUMBER, INCREMENT_BY from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'");
            while (rs.next()) {
                long lastNumber = rs.getLong("LAST_NUMBER");
                long incrementBy = rs.getLong("INCREMENT_BY");
                String sqlChangeIncrement = "alter sequence " + sequenceName + " increment by " +
                        (newSequenceValue - lastNumber);
                statementHandler.handle(sqlChangeIncrement);
                String sqlNextSequenceValue = "select " + sequenceName + ".NEXTVAL from DUAL";
                statementHandler.handle(sqlNextSequenceValue);
                String sqlResetIncrement = "alter sequence " + sequenceName + " increment by " + incrementBy;
                statementHandler.handle(sqlResetIncrement);
            }
        } finally {
            DbUtils.closeQuietly(null, st, rs);
        }
    }

    public boolean supportsIdentityColumns() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void incrementIdentityColumnToValue(String tableName, String primaryKeyColumnName, long identityValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void disableForeignKeyConstraintsCheckingOnConnection(Connection conn) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void removeNotNullConstraint(String tableName, String columnName) throws StatementHandlerException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private Set<String> getDbItemsOfType(String dbItemName,
                                          String systemMetadataTableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select " + dbItemName + " from " + systemMetadataTableName);
            Set<String> sequenceNames = new HashSet<String>();
            while (rs.next()) {
                sequenceNames.add(rs.getString(dbItemName));
            }
            return sequenceNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    public Set<String> getTableConstraintNames(String tableName) throws SQLException {
        Connection conn = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select CONSTRAINT_NAME from USER_CONSTRAINTS where TABLE_NAME = " +
                    tableName + " and CONSTRAINT_TYPE <> 'P'");
            Set<String> constraintNames = new HashSet<String>();
            while (rs.next()) {
                constraintNames.add(rs.getString("CONSTRAINT_NAME"));
            }
            return constraintNames;
        } finally {
            DbUtils.closeQuietly(conn, st, rs);
        }
    }

    public void disableConstraint(String tableName, String constraintName) throws StatementHandlerException {
        statementHandler.handle("alter table " + tableName + " disable constraint " + constraintName);
    }

    public String getLongDataType() {
        return "INTEGER";
    }
}
