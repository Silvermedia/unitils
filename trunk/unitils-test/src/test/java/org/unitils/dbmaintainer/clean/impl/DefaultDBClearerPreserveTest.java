/*
 * Copyright Unitils.org
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
package org.unitils.dbmaintainer.clean.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbmaintain.dbsupport.DbSupport;
import org.hsqldb.Trigger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Properties;

import static org.dbmaintain.config.DbMaintainProperties.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.core.util.SQLTestUtils.*;
import static org.unitils.database.DatabaseUnitils.clearDatabase;
import static org.unitils.database.DatabaseUnitils.getDbSupports;
import static org.unitils.database.SQLUnitils.executeUpdate;
import static org.unitils.database.SQLUnitils.executeUpdateQuietly;
import static org.unitils.testutil.TestUnitilsConfiguration.*;

/**
 * Test class for clearing the database with configuration to preserve all items.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 * @author Scott Prater
 */
public class DefaultDBClearerPreserveTest {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(DefaultDBClearerPreserveTest.class);

    private DataSource dataSource;
    private DbSupport defaultDbSupport;
    private String versionTableName;


    @Before
    public void initialize() throws Exception {
        // configure items to preserve
        Properties configuration = getUnitilsConfiguration();

        defaultDbSupport = getDbSupports().getDefaultDbSupport();
        configuration.setProperty(PROPERTY_PRESERVE_TABLES, "Test_Table, " + defaultDbSupport.quoted("Test_CASE_Table"));
        configuration.setProperty(PROPERTY_PRESERVE_VIEWS, "Test_View, " + defaultDbSupport.quoted("Test_CASE_View"));
        if (defaultDbSupport.supportsMaterializedViews()) {
            configuration.setProperty(PROPERTY_PRESERVE_MATERIALIZED_VIEWS, "Test_MView, " + defaultDbSupport.quoted("Test_CASE_MView"));
        }
        if (defaultDbSupport.supportsSequences()) {
            configuration.setProperty(PROPERTY_PRESERVE_SEQUENCES, "Test_Sequence, " + defaultDbSupport.quoted("Test_CASE_Sequence"));
        }
        if (defaultDbSupport.supportsSynonyms()) {
            configuration.setProperty(PROPERTY_PRESERVE_SYNONYMS, "Test_Synonym, " + defaultDbSupport.quoted("Test_CASE_Synonym"));
        }

        reinitializeUnitils(configuration);
        versionTableName = configuration.getProperty(PROPERTY_EXECUTED_SCRIPTS_TABLE_NAME);
        defaultDbSupport = getDbSupports().getDefaultDbSupport();
        dataSource = defaultDbSupport.getDataSource();

        cleanupTestDatabase();
        createTestDatabase();
    }

    @After
    public void cleanUp() throws Exception {
        resetUnitils();
        cleanupTestDatabase();
    }


    @Test
    public void preserveTables() throws Exception {
        assertEquals(2, defaultDbSupport.getTableNames().size());
        clearDatabase();
        assertEquals(2, defaultDbSupport.getTableNames().size());
    }

    @Test
    public void preserveViews() throws Exception {
        assertEquals(2, defaultDbSupport.getViewNames().size());
        clearDatabase();
        assertEquals(2, defaultDbSupport.getViewNames().size());
    }

    @Test
    public void preserveMaterializedViews() throws Exception {
        if (!defaultDbSupport.supportsMaterializedViews()) {
            logger.warn("Current dialect does not support materialized views. Skipping test.");
            return;
        }
        assertEquals(2, defaultDbSupport.getMaterializedViewNames().size());
        clearDatabase();
        assertEquals(2, defaultDbSupport.getMaterializedViewNames().size());
    }

    @Test
    public void preserveSynonyms() throws Exception {
        if (!defaultDbSupport.supportsSynonyms()) {
            logger.warn("Current dialect does not support synonyms. Skipping test.");
            return;
        }
        assertEquals(2, defaultDbSupport.getSynonymNames().size());
        clearDatabase();
        assertEquals(2, defaultDbSupport.getSynonymNames().size());
    }

    @Test
    public void preserveSequences() throws Exception {
        if (!defaultDbSupport.supportsSequences()) {
            logger.warn("Current dialect does not support sequences. Skipping test.");
            return;
        }
        assertEquals(2, defaultDbSupport.getSequenceNames().size());
        clearDatabase();
        assertEquals(2, defaultDbSupport.getSequenceNames().size());
    }


    private void createTestDatabase() throws Exception {
        String dialect = defaultDbSupport.getDatabaseInfo().getDialect();
        if ("hsqldb".equals(dialect)) {
            createTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            createTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            createTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            createTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            createTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            createTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            createTestDatabaseMsSql();
        } else {
            fail("This test is not implemented for current dialect: " + dialect);
        }
    }

    private void cleanupTestDatabase() throws Exception {
        executeUpdateQuietly("drop table " + versionTableName, dataSource);

        String dialect = defaultDbSupport.getDatabaseInfo().getDialect();
        if ("hsqldb".equals(dialect)) {
            cleanupTestDatabaseHsqlDb();
        } else if ("mysql".equals(dialect)) {
            cleanupTestDatabaseMySql();
        } else if ("oracle".equals(dialect)) {
            cleanupTestDatabaseOracle();
        } else if ("postgresql".equals(dialect)) {
            cleanupTestDatabasePostgreSql();
        } else if ("db2".equals(dialect)) {
            cleanupTestDatabaseDb2();
        } else if ("derby".equals(dialect)) {
            cleanupTestDatabaseDerby();
        } else if ("mssql".equals(dialect)) {
            cleanupTestDatabaseMsSql();
        }
    }

    //
    // Database setup for HsqlDb
    //

    private void createTestDatabaseHsqlDb() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" call \"org.unitils.core.dbsupport.HsqldbDbSupportTest.TestTrigger\"", dataSource);
    }

    private void cleanupTestDatabaseHsqlDb() throws Exception {
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
    }

    /**
     * Test trigger for hypersonic.
     *
     * @author Filip Neven
     * @author Tim Ducheyne
     */
    public static class TestTrigger implements Trigger {

        public void fire(int i, String string, String string1, Object[] objects, Object[] objects1) {
        }
    }

    //
    // Database setup for MySql
    //

    private void createTestDatabaseMySql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key AUTO_INCREMENT, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table `Test_CASE_Table` (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view `Test_CASE_View` as select col1 from `Test_CASE_Table`", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
        executeUpdate("create trigger `Test_CASE_Trigger` after insert on `Test_CASE_Table` FOR EACH ROW begin end", dataSource);
    }

    private void cleanupTestDatabaseMySql() throws Exception {
        dropTestTables(defaultDbSupport, "test_table", "`Test_CASE_Table`");
        dropTestViews(defaultDbSupport, "test_view", "`Test_CASE_View`");
        dropTestTriggers(defaultDbSupport, "test_trigger", "`Test_CASE_Trigger`");
    }

    //
    // Database setup for Oracle
    //

    private void createTestDatabaseOracle() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create materialized views
        executeUpdate("create materialized view test_mview as select col1 from test_table", dataSource);
        executeUpdate("create materialized view \"Test_CASE_MView\" as select col1 from test_table", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create or replace trigger test_trigger before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end test_trigger", dataSource);
        executeUpdate("create or replace trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" begin dbms_output.put_line('test'); end \"Test_CASE_Trigger\"", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }

    private void cleanupTestDatabaseOracle() throws Exception {
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestMaterializedViews(defaultDbSupport, "test_mview", "\"Test_CASE_MView\"");
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for PostgreSql
    //

    private void createTestDatabasePostgreSql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 varchar(10) not null primary key, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 varchar(10), foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        try {
            executeUpdate("create language plpgsql", dataSource);
        } catch (Exception e) {
            // ignore language already exists
        }
        executeUpdate("create or replace function test() returns trigger as $$ declare begin end; $$ language plpgsql", dataSource);
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW EXECUTE PROCEDURE test()", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int)", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int)", dataSource);
    }

    private void cleanupTestDatabasePostgreSql() throws Exception {
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Db2
    //

    private void createTestDatabaseDb2() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create sequences
        executeUpdate("create sequence test_sequence", dataSource);
        executeUpdate("create sequence \"Test_CASE_Sequence\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" before insert on \"Test_CASE_Table\" FOR EACH ROW when (1 < 0) SIGNAL SQLSTATE '0'", dataSource);
        // create types
        executeUpdate("create type test_type AS (col1 int) MODE DB2SQL", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" AS (col1 int) MODE DB2SQL", dataSource);
    }

    private void cleanupTestDatabaseDb2() throws Exception {
        dropTestTables(defaultDbSupport, "test_table", "\"Test_CASE_Table\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestSequences(defaultDbSupport, "test_sequence", "\"Test_CASE_Sequence\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
    }

    //
    // Database setup for Derby
    //

    private void createTestDatabaseDerby() throws Exception {
        // create tables
        executeUpdate("create table \"TEST_TABLE\" (col1 int not null primary key generated by default as identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("call SYSCS_UTIL.SYSCS_SET_DATABASE_PROPERTY('testKey', 'test')", dataSource);
        executeUpdate("create trigger test_trigger no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" no cascade before insert on \"Test_CASE_Table\" FOR EACH ROW MODE DB2SQL VALUES SYSCS_UTIL.SYSCS_GET_DATABASE_PROPERTY('testKey')", dataSource);
    }


    /**
     * First drop the views, since Derby doesn't support "drop table ... cascade" (yet, as of Derby 10.3)
     */
    private void cleanupTestDatabaseDerby() throws Exception {
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDbSupport, "\"Test_CASE_Table\"", "test_table");
    }

    //
    // Database setup for MS-Sql
    //

    private void createTestDatabaseMsSql() throws Exception {
        // create tables
        executeUpdate("create table test_table (col1 int not null primary key identity, col2 varchar(12) not null)", dataSource);
        executeUpdate("create table \"Test_CASE_Table\" (col1 int, foreign key (col1) references test_table(col1))", dataSource);
        // create views
        executeUpdate("create view test_view as select col1 from test_table", dataSource);
        executeUpdate("create view \"Test_CASE_View\" as select col1 from \"Test_CASE_Table\"", dataSource);
        // create synonyms
        executeUpdate("create synonym test_synonym for test_table", dataSource);
        executeUpdate("create synonym \"Test_CASE_Synonym\" for \"Test_CASE_Table\"", dataSource);
        // create triggers
        executeUpdate("create trigger test_trigger on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        executeUpdate("create trigger \"Test_CASE_Trigger\" on \"Test_CASE_Table\" after insert AS select * from test_table", dataSource);
        // create types
        executeUpdate("create type test_type from int", dataSource);
        executeUpdate("create type \"Test_CASE_Type\" from int", dataSource);
    }

    private void cleanupTestDatabaseMsSql() throws Exception {
        dropTestSynonyms(defaultDbSupport, "test_synonym", "\"Test_CASE_Synonym\"");
        dropTestViews(defaultDbSupport, "test_view", "\"Test_CASE_View\"");
        dropTestTriggers(defaultDbSupport, "test_trigger", "\"Test_CASE_Trigger\"");
        dropTestTables(defaultDbSupport, "\"Test_CASE_Table\"", "test_table");
        dropTestTypes(defaultDbSupport, "test_type", "\"Test_CASE_Type\"");
    }
}
