/*
 * Copyright 2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.unitils.core.dbsupport;

import org.unitils.core.UnitilsException;
import static org.unitils.core.util.SQLUtils.*;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

/**
 * Implementation of {@link DbSupport} for an Oracle database.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class OracleDbSupport extends DbSupport {


    /**
     * Creates support for Oracle databases.
     */
    public OracleDbSupport() {
        super("oracle");
    }


    /**
     * Returns the names of all tables in the database.
     *
     * @return The names of all tables in the database
     */
    @Override
    public Set<String> getTableNames() {
        return getItemsAsStringSet("select TABLE_NAME from USER_TABLES", getDataSource());
    }


    /**
     * Retrieves the names of all the views in the database schema.
     *
     * @return The names of all views in the database
     */
    @Override
    public Set<String> getViewNames() {
        return getItemsAsStringSet("select VIEW_NAME from USER_VIEWS", getDataSource());
    }


    /**
     * Retrieves the names of all the synonyms in the database schema.
     *
     * @return The names of all synonyms in the database
     */
    @Override
    public Set<String> getSynonymNames() {
        return getItemsAsStringSet("select SYNONYM_NAME from USER_SYNONYMS", getDataSource());
    }


    /**
     * Retrieves the names of all the sequences in the database schema.
     *
     * @return The names of all sequences in the database
     */
    @Override
    public Set<String> getSequenceNames() {
        return getItemsAsStringSet("select SEQUENCE_NAME from USER_SEQUENCES", getDataSource());
    }


    /**
     * Retrieves the names of all the triggers in the database schema.
     *
     * @return The names of all triggers in the database
     */
    @Override
    public Set<String> getTriggerNames() {
        return getItemsAsStringSet("select TRIGGER_NAME from USER_TRIGGERS triggers, USER_TABLES tables where tables.TABLE_NAME = triggers.TABLE_NAME", getDataSource());
    }


    /**
     * Retrieves the names of all the types in the database schema.
     *
     * @return The names of all types in the database
     */
    @Override
    public Set<String> getTypeNames() {
        return getItemsAsStringSet("select TYPE_NAME from USER_TYPES", getDataSource());
    }


    /**
     * Removes the table with the given name from the database.
     * Note: the table name is surrounded with quotes, making it case-sensitive.
     *
     * @param tableName The table to drop (case-sensitive), not null
     */
    @Override
    public void dropTable(String tableName) {
        executeUpdate("drop table " + qualified(tableName) + " cascade constraints", getDataSource());
    }


    /**
     * Removes the view with the given name from the database
     * Note: the view name is surrounded with quotes, making it case-sensitive.
     *
     * @param viewName The view to drop (case-sensitive), not null
     */
    @Override
    public void dropView(String viewName) {
        executeUpdate("drop view " + qualified(viewName) + " cascade constraints", getDataSource());
    }


    /**
     * Drops the type with the given name from the database
     * Note: the type name is surrounded with quotes, making it case-sensitive.
     *
     * @param typeName The type to drop (case-sensitive), not null
     */
    @Override
    public void dropType(String typeName) {
        executeUpdate("drop type " + qualified(typeName) + " force", getDataSource());
    }


    /**
     * Returns the value of the sequence with the given name
     *
     * @param sequenceName The sequence, not null
     * @return The value of the sequence with the given name
     */
    @Override
    public long getCurrentValueOfSequence(String sequenceName) {
        return getItemAsLong("select LAST_NUMBER from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'", getDataSource());
    }


    /**
     * Returns the foreign key and not null constraint names that are enabled/enforced for the table with the given name
     *
     * @param tableName The table, not null
     * @return The set of constraint names, not null
     */
    @Override
    public Set<String> getTableConstraintNames(String tableName) {
        return getItemsAsStringSet("select CONSTRAINT_NAME from USER_CONSTRAINTS where TABLE_NAME = '" + tableName + "' and (CONSTRAINT_TYPE = 'R' or CONSTRAINT_TYPE = 'C') and STATUS = 'ENABLED'", getDataSource());
    }


    /**
     * Sets the next value of the sequence with the given sequence name to the given sequence value.
     *
     * @param sequenceName     The sequence, not null
     * @param newSequenceValue The value to set
     */
    @Override
    public void incrementSequenceToValue(String sequenceName, long newSequenceValue) {
        Connection conn = null;
        ResultSet rs = null;
        Statement st = null;
        try {
            conn = getDataSource().getConnection();
            st = conn.createStatement();
            rs = st.executeQuery("select LAST_NUMBER, INCREMENT_BY from USER_SEQUENCES where SEQUENCE_NAME = '" + sequenceName + "'");
            while (rs.next()) {
                long lastNumber = rs.getLong("LAST_NUMBER");
                long incrementBy = rs.getLong("INCREMENT_BY");
                String sqlChangeIncrement = "alter sequence " + qualified(sequenceName) + " increment by " + (newSequenceValue - lastNumber);
                executeUpdate(sqlChangeIncrement, getDataSource());
                String sqlNextSequenceValue = "select " + qualified(sequenceName) + ".NEXTVAL from DUAL";
                executeUpdate(sqlNextSequenceValue, getDataSource());
                String sqlResetIncrement = "alter sequence " + qualified(sequenceName) + " increment by " + incrementBy;
                executeUpdate(sqlResetIncrement, getDataSource());
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while incrementing sequence to value", e);
        } finally {
            closeQuietly(conn, st, rs);
        }
    }


    /**
     * Disables the constraint with the given name on table with the given name.
     *
     * @param tableName      The table with the constraint, not null
     * @param constraintName The constraint, not null
     */
    @Override
    public void disableConstraint(String tableName, String constraintName) {
        executeUpdate("alter table " + qualified(tableName) + " disable constraint " + constraintName, getDataSource());
    }


    /**
     * Gets the column type suitable to store values of the Java <code>java.lang.Long</code> type.
     *
     * @return The column type
     */
    @Override
    public String getLongDataType() {
        return "INTEGER";
    }


    /**
     * Synonyms are supported
     *
     * @return True
     */
    @Override
    public boolean supportsSynonyms() {
        return true;
    }


    /**
     * Sequences are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsSequences() {
        return true;
    }


    /**
     * Triggers are supported.
     *
     * @return True
     */
    @Override
    public boolean supportsTriggers() {
        return true;
    }


    /**
     * Types are supported
     *
     * @return true
     */
    @Override
    public boolean supportsTypes() {
        return true;
    }

}