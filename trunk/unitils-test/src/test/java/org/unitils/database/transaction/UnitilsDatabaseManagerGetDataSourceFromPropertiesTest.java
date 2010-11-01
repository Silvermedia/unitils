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
package org.unitils.database.transaction;

import org.dbmaintain.database.Database;
import org.dbmaintain.database.DatabaseConnection;
import org.dbmaintain.database.DatabaseException;
import org.dbmaintain.database.Databases;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class UnitilsDatabaseManagerGetDataSourceFromPropertiesTest extends UnitilsJUnit4 {

    /* Tested object */
    private UnitilsDatabaseManager unitilsDatabaseManager;

    protected Mock<DbMaintainManager> dbMaintainManager;
    protected Mock<Databases> databases;
    protected Mock<Database> database;

    protected Mock<DatabaseConnection> databaseConnection;
    @Dummy
    protected DataSource dataSource;


    @Before
    public void initialize() {
        unitilsDatabaseManager = new UnitilsDatabaseManager(false, dbMaintainManager.getMock());
        databaseConnection.returns(dataSource).getDataSource();
    }


    @Test
    public void dataSourceForDatabaseWithName() throws Exception {
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection("database1");

        DataSource result = unitilsDatabaseManager.getDataSource("database1", null);
        assertSame(dataSource, result);
    }

    @Test
    public void defaultDataSource() throws Exception {
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(isNull(String.class));

        DataSource result = unitilsDatabaseManager.getDataSource(null, null);
        assertSame(dataSource, result);
    }

    @Test
    public void unknownDatabaseName() throws Exception {
        try {
            dbMaintainManager.raises(DatabaseException.class).getDatabaseConnection("xxxx");

            unitilsDatabaseManager.getDataSource("xxxx", null);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            // expected
        }
    }

    @Test
    public void dataSourceWrappedInTransactionAwareProxy() throws Exception {
        unitilsDatabaseManager = new UnitilsDatabaseManager(true, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, null);
        assertTrue(dataSource instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void disableWrappingInTransactionAwareProxy() throws Exception {
        unitilsDatabaseManager = new UnitilsDatabaseManager(false, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        DataSource dataSource = unitilsDatabaseManager.getDataSource(null, null);
        assertFalse(dataSource instanceof TransactionAwareDataSourceProxy);
    }

    @Test
    public void sameWrappedDataSourceReturned() throws Exception {
        unitilsDatabaseManager = new UnitilsDatabaseManager(true, dbMaintainManager.getMock());
        dbMaintainManager.returns(databaseConnection).getDatabaseConnection(null);

        DataSource dataSource1 = unitilsDatabaseManager.getDataSource(null, null);
        DataSource dataSource2 = unitilsDatabaseManager.getDataSource(null, null);
        assertSame(dataSource1, dataSource2);
    }
}
