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
package org.unitils.dbmaintainer.clean;

import org.unitils.UnitilsJUnit3;
import org.unitils.core.ConfigurationLoader;
import org.unitils.database.annotations.TestDataSource;
import org.unitils.dbmaintainer.clean.impl.DefaultDBClearer;
import org.unitils.dbmaintainer.dbsupport.DbSupport;
import org.unitils.dbmaintainer.script.StatementHandler;
import org.unitils.dbmaintainer.script.impl.StatementHandlerException;
import static org.unitils.dbmaintainer.util.DatabaseModuleConfigUtils.*;
import static org.unitils.thirdparty.org.apache.commons.dbutils.DbUtils.closeQuietly;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Base test class for the DBClearer. Contains tests that can be implemented generally for all different database dialects.
 * Extended with implementations for each supported database dialect.
 * <p/>
 * Tests are only executed for the currently activated database dialect. By default, a hsqldb in-memory database is used,
 * to avoid the need for setting up a database instance. If you want to run unit tests for other dbms's, change the
 * configuration in test/resources/unitils.properties
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
abstract public class DBClearerTest extends UnitilsJUnit3 {

    /* DataSource for the test database, is injected */
    @TestDataSource
    protected DataSource dataSource = null;

    /* Tested object */
    private DBClearer dbClearer;

    /* The DbSupport object */
    private DbSupport dbSupport;


    /**
     * Configures the tested object. Creates a test table, index, view and sequence
     */
    @Override
    protected void setUp() throws Exception {
        if (isTestedDialectActivated()) {
            super.setUp();

            // case insensitive names
            String itemsToPreserve = "Test_table_Preserve, Test_view_Preserve, Test_sequence_Preserve, Test_trigger_Preserve, ";
            // case sensitive names
            itemsToPreserve += "\"Test_CASE_Table_Preserve\", \"Test_CASE_View_Preserve\", \"Test_CASE_Sequence_Preserve\", \"Test_CASE_Trigger_Preserve\"";

            Properties configuration = new ConfigurationLoader().loadConfiguration();
            configuration.setProperty(DefaultDBClearer.PROPKEY_ITEMSTOPRESERVE, itemsToPreserve);

            StatementHandler statementHandler = getConfiguredStatementHandlerInstance(configuration, dataSource);
            dbSupport = getConfiguredDbSupportInstance(configuration, dataSource, statementHandler);
            dbClearer = getConfiguredDatabaseTaskInstance(DBClearer.class, configuration, dataSource, statementHandler);

            cleanupTestDatabase();
            createTestDatabase();
        }
    }


    protected void tearDown() throws Exception {
        if (isTestedDialectActivated()) {
            super.tearDown();
            cleanupTestDatabase();
        }
    }


    /**
     * Checks if the tables are correctly dropped.
     */
    public void testClearDatabase_tables() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(tableExists("TEST_TABLE"));
            assertTrue(tableExists("TEST_TABLE_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertTrue(tableExists(dbSupport.quoted("Test_CASE_Table")));
                assertTrue(tableExists(dbSupport.quoted("Test_CASE_Table_Preserve")));
            }
            dbClearer.clearSchema();
            assertFalse(tableExists("TEST_TABLE"));
            assertTrue(tableExists("TEST_TABLE_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertFalse(tableExists(dbSupport.quoted("Test_CASE_Table")));
                assertTrue(tableExists(dbSupport.quoted("Test_CASE_Table_Preserve")));
            }
        }
    }


    /**
     * Checks if the views are correctly dropped
     */
    public void testClearDatabase_views() throws Exception {
        if (isTestedDialectActivated()) {
            assertTrue(viewExists("TEST_VIEW"));
            assertTrue(viewExists("TEST_VIEW_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertTrue(viewExists(dbSupport.quoted("Test_CASE_View")));
                assertTrue(viewExists(dbSupport.quoted("Test_CASE_View_Preserve")));
            }
            dbClearer.clearSchema();
            assertFalse(viewExists("TEST_VIEW"));
            assertTrue(viewExists("TEST_VIEW_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertFalse(viewExists(dbSupport.quoted("Test_CASE_View")));
                assertTrue(viewExists(dbSupport.quoted("Test_CASE_View_Preserve")));
            }
        }
    }


    /**
     * Tests if the triggers are correctly dropped
     */
    public void testClearDatabase_sequences() throws Exception {
        if (isTestedDialectActivated() && dbSupport.supportsSequences()) {
            assertTrue(sequenceExists("TEST_SEQUENCE"));
            assertTrue(sequenceExists("TEST_SEQUENCE_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertTrue(sequenceExists(dbSupport.quoted("Test_CASE_Sequence")));
                assertTrue(sequenceExists(dbSupport.quoted("Test_CASE_Sequence_Preserve")));
            }
            dbClearer.clearSchema();
            assertFalse(sequenceExists("TEST_SEQUENCE"));
            assertTrue(sequenceExists("TEST_SEQUENCE_PRESERVE"));
            if (dbSupport.supportsQuotedDatabaseObjectNames()) {
                assertFalse(sequenceExists(dbSupport.quoted("Test_CASE_Sequence")));
                assertTrue(sequenceExists(dbSupport.quoted("Test_CASE_Sequence_Preserve")));
            }
        }
    }


    /**
     * Tests if the triggers are correctly dropped
     */
    public void testClearDatabase_triggers() throws Exception {
        if (isTestedDialectActivated() && dbSupport.supportsTriggers()) {
            assertTrue(triggerExists("TEST_TRIGGER"));
            assertTrue(triggerExists("TEST_TRIGGER_PRESERVE"));
            assertTrue(triggerExists(dbSupport.quoted("Test_CASE_Trigger")));
            assertTrue(triggerExists(dbSupport.quoted("Test_CASE_Trigger_Preserve")));
            dbClearer.clearSchema();
            assertFalse(triggerExists("TEST_TRIGGER"));
            assertTrue(triggerExists("TEST_TRIGGER_PRESERVE"));
            assertFalse(triggerExists(dbSupport.quoted("Test_CASE_Trigger")));
            assertTrue(triggerExists(dbSupport.quoted("Test_CASE_Trigger_Preserve")));
        }
    }


    /**
     * Creates a trigger for the test.
     *
     * @param tableName   The table for the trigger, not null
     * @param triggerName The name of the trigger, not null
     */
    abstract protected void createTestTrigger(String tableName, String triggerName) throws SQLException;


    /**
     * Checks whether the database dialect that is tested in the current implementation is the currenlty configured
     * database dialect.
     *
     * @return True if the tested dbms is equal to the configured one, false otherwise
     */
    abstract protected boolean isTestedDialectActivated();


    /**
     * Checks whether the table with the given name exists
     *
     * @param tableName The name of the table, not null
     * @return True if the table with the given name exists, false otherwise
     */
    public boolean tableExists(String tableName) throws SQLException {
        String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
        return dbSupport.getTableNames().contains(correctCaseTableName);
    }


    /**
     * Checks whether the table with the given name exists
     *
     * @param viewName The name of the view, not null
     * @return True if the view with the given name exists, false otherwise
     */
    public boolean viewExists(String viewName) throws SQLException {
        String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(viewName);
        return dbSupport.getViewNames().contains(correctCaseViewName);
    }


    /**
     * Checks whether the trigger with the given name exists
     *
     * @param triggerName The name of the trigger, not null
     * @return True if the trigger with the given name exists, false otherwise
     */
    public boolean triggerExists(String triggerName) throws SQLException {
        String correctCaseTriggerName = dbSupport.toCorrectCaseIdentifier(triggerName);
        return dbSupport.getTriggerNames().contains(correctCaseTriggerName);
    }


    /**
     * Checks whether the sequence with the given name exists
     *
     * @param sequenceName The name of the sequence, not null
     * @return True if the sequence with the given name exists, false otherwise
     */
    public boolean sequenceExists(String sequenceName) throws SQLException {
        String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier(sequenceName);
        return dbSupport.getSequenceNames().contains(correctCaseSequenceName);
    }


    /**
     * Creates all test database structures (view, tables...)
     */
    private void createTestDatabase() throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = dataSource.getConnection();
            st = conn.createStatement();
            // create tables
            st.execute("create table TEST_TABLE (col1 varchar(10))");
            st.execute("create table TEST_TABLE_PRESERVE (col1 varchar(10))");
            st.execute("create table " + dbSupport.quoted("Test_CASE_Table") + " (col1 varchar(10))");
            st.execute("create table " + dbSupport.quoted("Test_CASE_Table_Preserve") + " (col1 varchar(10))");
            // create views
            st.execute("create view TEST_VIEW as select col1 from TEST_TABLE_PRESERVE");
            st.execute("create view TEST_VIEW_PRESERVE as select col1 from TEST_TABLE_PRESERVE");
            st.execute("create view " + dbSupport.quoted("Test_CASE_View") + " as select col1 from " + dbSupport.quoted("Test_CASE_Table"));
            st.execute("create view " + dbSupport.quoted("Test_CASE_View_Preserve") + " as select col1 from " + dbSupport.quoted("Test_CASE_Table_Preserve"));
            // create sequences
            if (dbSupport.supportsSequences()) {
                st.execute("create sequence TEST_SEQUENCE");
                st.execute("create sequence TEST_SEQUENCE_PRESERVE");
                st.execute("create sequence " + dbSupport.quoted("Test_CASE_Sequence"));
                st.execute("create sequence " + dbSupport.quoted("Test_CASE_Sequence_Preserve"));
            }
            // create triggers
            if (dbSupport.supportsTriggers()) {
                createTestTrigger("TEST_TABLE", "TEST_TRIGGER");
                createTestTrigger("TEST_TABLE_PRESERVE", "TEST_TRIGGER_PRESERVE");
                createTestTrigger(dbSupport.quoted("Test_CASE_Table"), dbSupport.quoted("Test_CASE_Trigger"));
                createTestTrigger(dbSupport.quoted("Test_CASE_Table_Preserve"), dbSupport.quoted("Test_CASE_Trigger_Preserve"));
            }
        } finally {
            closeQuietly(conn, st, null);
        }
    }


    /**
     * Drops all created test database structures (views, tables...)
     */
    protected void cleanupTestDatabase() throws Exception {
        dropTestTables("TEST_TABLE", "TEST_TABLE_PRESERVE", dbSupport.quoted("Test_CASE_Table"), dbSupport.quoted("Test_CASE_Table_Preserve"));
        dropTestViews("TEST_VIEW", "TEST_VIEW_PRESERVE", dbSupport.quoted("Test_CASE_View"), dbSupport.quoted("Test_CASE_View_Preserve"));
        dropTestSequences("TEST_SEQUENCE", "TEST_SEQUENCE_PRESERVE", dbSupport.quoted("Test_CASE_Sequence"), dbSupport.quoted("Test_CASE_Sequence_Preserve"));
        dropTestTriggers("TEST_TRIGGER", "TEST_TRIGGER_PRESERVE", dbSupport.quoted("Test_CASE_Trigger"), dbSupport.quoted("Test_CASE_Trigger_Preserve"));
    }


    /**
     * Drops the test tables
     *
     * @param tableNames The tables to drop
     */
    private void dropTestTables(String... tableNames) {
        for (String tableName : tableNames) {
            try {
                String correctCaseTableName = dbSupport.toCorrectCaseIdentifier(tableName);
                dbSupport.dropTable(correctCaseTableName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test views
     *
     * @param viewNames The views to drop
     */
    private void dropTestViews(String... viewNames) {
        for (String viewName : viewNames) {
            try {
                String correctCaseViewName = dbSupport.toCorrectCaseIdentifier(viewName);
                dbSupport.dropView(correctCaseViewName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test sequence
     *
     * @param sequenceNames The sequences to drop
     */
    private void dropTestSequences(String... sequenceNames) {
        if (!dbSupport.supportsSequences()) {
            return;
        }
        for (String sequenceName : sequenceNames) {
            try {
                String correctCaseSequenceName = dbSupport.toCorrectCaseIdentifier(sequenceName);
                dbSupport.dropSequence(correctCaseSequenceName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }


    /**
     * Drops the test triggers
     *
     * @param triggerNames The triggers to drop
     */
    private void dropTestTriggers(String... triggerNames) {
        if (!dbSupport.supportsTriggers()) {
            return;
        }
        for (String triggerName : triggerNames) {
            try {
                String correctCaseTriggerName = dbSupport.toCorrectCaseIdentifier(triggerName);
                dbSupport.dropTrigger(correctCaseTriggerName);
            } catch (StatementHandlerException e) {
                // Ignored
            }
        }
    }
}