/*
 * Copyright 2013,  Unitils.org
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
package org.unitils.database;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlAssertAssertIntegerIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
        executeUpdate("create table my_table (value int, other varchar)");
        executeUpdate("create table my_table (value int)", "database2");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void assertionSuccessfulForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)");
        SqlAssert.assertInteger(111, "select value from my_table");
    }

    @Test
    public void assertionFailedForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)");
        try {
            SqlAssert.assertInteger(222, "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table': expected:<222> but was:<111>", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)", "database2");
        SqlAssert.assertInteger(111, "select value from my_table", "database2");
    }

    @Test
    public void assertionFailedForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)", "database2");
        try {
            SqlAssert.assertInteger(222, "select value from my_table", "database2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table': expected:<222> but was:<111>", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenQueryDoesNotProduceAnyResults() throws Exception {
        try {
            SqlAssert.assertInteger(111, "select value from my_table");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value. Statement did not produce any results: 'select value from my_table'.\n" +
                    "Reason: EmptyResultDataAccessException: Incorrect result size: expected 1, actual 0", e.getMessage());
        }
    }

    @Test
    public void assertionFailedForNullResult() throws Exception {
        executeUpdate("insert into my_table (value) values (null)");
        try {
            SqlAssert.assertInteger(111, "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table': expected:<111> but was:<null>", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNonIntegerValue() throws Exception {
        executeUpdate("insert into my_table (other) values ('xxx')");
        try {
            SqlAssert.assertInteger(111, "select other from my_table");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select other from my_table'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select other from my_table]; nested exception is java.sql.SQLSyntaxErrorException: incompatible data type in conversion: from SQL type VARCHAR to java.lang.Integer, value: xxx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlAssert.assertInteger(111, "select value from my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlAssert.assertInteger(111, "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLSyntaxErrorException: unexpected token: XXX", e.getMessage());
        }
    }
}
