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

import java.util.Arrays;
import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlAssertAssertLongListIntegrationTest {

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
        executeUpdate("insert into my_table (value) values (222)");
        SqlAssert.assertLongList(asList(111L, 222L), "select value from my_table");
    }

    @Test
    public void orderIsIgnored() throws Exception {
        executeUpdate("insert into my_table (value) values (111)");
        executeUpdate("insert into my_table (value) values (222)");
        SqlAssert.assertLongList(asList(222L, 111L), "select value from my_table");
    }

    @Test
    public void assertionFailedForDefaultDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)");
        try {
            SqlAssert.assertLongList(asList(111L, 222L), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [111, 222], actual: [111]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [111, 222]\n" +
                    "   actual: [111]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)", "database2");
        executeUpdate("insert into my_table (value) values (222)", "database2");
        SqlAssert.assertLongList(asList(111L, 222L), "select value from my_table", "database2");
    }

    @Test
    public void assertionFailedForNamedDatabase() throws Exception {
        executeUpdate("insert into my_table (value) values (111)", "database2");
        try {
            SqlAssert.assertLongList(asList(111L, 222L), "select value from my_table", "database2");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [111, 222], actual: [111]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [111, 222]\n" +
                    "   actual: [111]\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForEmptyResult() throws Exception {
        SqlAssert.assertLongList(Collections.<Long>emptyList(), "select value from my_table");
    }

    @Test
    public void assertionFailedForEmptyResult() throws Exception {
        try {
            SqlAssert.assertLongList(asList(111L, 222L), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [111, 222], actual: []\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 0.\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [111, 222]\n" +
                    "   actual: []\n\n", e.getMessage());
        }
    }

    @Test
    public void assertionSuccessfulForNullValue() throws Exception {
        executeUpdate("insert into my_table (value) values (null)");
        executeUpdate("insert into my_table (value) values (null)");
        SqlAssert.assertLongList(Arrays.<Long>asList(null, null), "select value from my_table");
    }

    @Test
    public void assertionFailedForNullValue() throws Exception {
        executeUpdate("insert into my_table (value) values (111)");
        try {
            SqlAssert.assertLongList(Arrays.<Long>asList(null, null), "select value from my_table");
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("Different result found for query 'select value from my_table':\n" +
                    "Expected: [null, null], actual: [111]\n" +
                    "\n" +
                    "--- Found following differences ---\n" +
                    "Collections have a different size: Expected 2, actual 1.\n" +
                    "[0,0]: expected: null, actual: 111\n" +
                    "[1,0]: expected: null, actual: 111\n" +
                    "\n" +
                    "--- Difference detail tree ---\n" +
                    " expected: [null, null]\n" +
                    "   actual: [111]\n" +
                    "\n" +
                    "[0,0] expected: null\n" +
                    "[0,0]   actual: 111\n" +
                    "\n" +
                    "[1,0] expected: null\n" +
                    "[1,0]   actual: 111\n\n", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNonLongValue() throws Exception {
        executeUpdate("insert into my_table (other) values ('xxx')");
        try {
            SqlAssert.assertLongList(asList(111L), "select other from my_table");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'select other from my_table'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [select other from my_table]; nested exception is java.sql.SQLSyntaxErrorException: incompatible data type in conversion: from SQL type VARCHAR to java.lang.Long, value: xxx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlAssert.assertLongList(asList(111L), "select value from my_table", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenInvalidStatement() throws Exception {
        try {
            SqlAssert.assertLongList(asList(111L), "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'xxx'.\n" +
                    "Reason: BadSqlGrammarException: StatementCallback; bad SQL grammar [xxx]; nested exception is java.sql.SQLSyntaxErrorException: unexpected token: XXX", e.getMessage());
        }
    }
}
