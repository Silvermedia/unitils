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
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class SqlUnitilsExecuteUpdateQuietlyIntegrationTest {

    @Before
    public void initialize() {
        cleanup();
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table", "database1");
        executeUpdateQuietly("drop table my_table", "database2");
    }


    @Test
    public void defaultDatabase() throws Exception {
        int result1 = SqlUnitils.executeUpdateQuietly("create table my_table (id int)");
        int result2 = SqlUnitils.executeUpdateQuietly("insert into my_table (id) values ('111')");

        assertTableCount(1, "my_table");
        assertEquals(0, result1);
        assertEquals(1, result2);
    }

    @Test
    public void namedDatabase() throws Exception {
        int result1 = SqlUnitils.executeUpdateQuietly("create table my_table (id int)", "database2");
        int result2 = SqlUnitils.executeUpdateQuietly("insert into my_table (id) values ('111')", "database2");

        assertTableCount(1, "my_table", "database2");
        assertEquals(0, result1);
        assertEquals(1, result2);
    }

    @Test
    public void exceptionWhenDatabaseNameNotFound() throws Exception {
        try {
            SqlUnitils.executeUpdateQuietly("create table my_table (id int)", "xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultDatabaseWhenNullDatabaseName() throws Exception {
        SqlUnitils.executeUpdateQuietly("create table my_table (id int)", null);
        SqlUnitils.executeUpdateQuietly("insert into my_table (id) values ('111')", null);

        assertTableCount(1, "my_table");
    }

    @Test
    public void ignoredWhenStatementFails() throws Exception {
        int result = SqlUnitils.executeUpdateQuietly("xxx");
        assertEquals(-1, result);
    }
}
