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

package org.unitils.database.dbmaintain;

import org.dbmaintain.database.DatabaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotation.TestDataSource;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerGetItemAsLongTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    @TestDataSource
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(null);

        executeUpdate("create table my_table (id int, value int)");
        executeUpdate("insert into my_table (id, value) values (10, 555)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void getItemAsLong() {
        long result = dbMaintainSQLHandler.getItemAsLong("select value from my_table where id = 10", dataSource);
        assertEquals(555, result);
    }

    @Test
    public void exceptionWhenFailure() {
        try {
            dbMaintainSQLHandler.getItemAsLong("xx", dataSource);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertEquals("Error while executing query:\n" +
                    "xx", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoValueFound() {
        try {
            dbMaintainSQLHandler.getItemAsLong("select value from my_table where id = 999", dataSource);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertEquals("No value found for query:\n" +
                    "select value from my_table where id = 999", e.getMessage());
        }
    }
}
