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
package org.unitils.dataset;

import org.junit.After;
import org.junit.Before;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotations.TestDataSource;

import javax.sql.DataSource;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.unitils.database.SQLUnitils.*;
import static org.unitils.dataset.DataSetUnitils.invalidateCachedDatabaseMetaData;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class DataSetTestBase extends UnitilsJUnit4 {

    @TestDataSource
    protected DataSource dataSource;


    @Before
    public void createTestTables() {
        dropTestTables();
        executeUpdate("create table test (col1 varchar(100) not null primary key, col2 integer, col3 timestamp, col4 varchar(100))", dataSource);
        executeUpdate("create table dependent (col1 varchar(100), foreign key (col1) references test(col1))", dataSource);
        invalidateCachedDatabaseMetaData();
    }

    @After
    public void dropTestTables() {
        executeUpdateQuietly("drop table dependent", dataSource);
        executeUpdateQuietly("drop table test", dataSource);
        invalidateCachedDatabaseMetaData();
    }


    protected void assertValueInTable(String tableName, String columnName, String expectedValue) {
        Set<String> values = getValues(columnName, tableName);
        assertTrue("Expected value " + expectedValue + " in table " + tableName + ", but found " + values, values.contains(expectedValue));
    }

    protected void assertValueNotInTable(String tableName, String columnName, String notExpectedValue) throws Exception {
        Set<String> values = getValues(columnName, tableName);
        assertFalse("Value " + notExpectedValue + " not expected in table " + tableName + ", but found ", values.contains(notExpectedValue));
    }

    protected Set<String> getValues(String columnName, String table) {
        return getItemsAsStringSet("select " + columnName + " from " + table, dataSource);
    }

    protected void insertValueInTableTest(String value) {
        executeUpdate("insert into test (col1) values ('" + value + "')", dataSource);
    }

    protected void insertValueInTableDependent(String value) {
        executeUpdate("insert into dependent (col1) values ('" + value + "')", dataSource);
    }

    protected void assertMessageContains(String part, Throwable e) {
        String message = e.getMessage();
        assertTrue("Exception message did not contain " + part + ".\nMessage: " + message, message.contains(part));
    }

    protected void assertMessageNotContains(String part, Throwable e) {
        String message = e.getMessage();
        assertFalse("Exception message should not have contained " + part + ".\nMessage: " + message, message.contains(part));
    }


}