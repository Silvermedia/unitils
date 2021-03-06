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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.annotation.TestDataSource;
import org.unitils.database.transaction.impl.DefaultTransactionProvider;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.database.SqlAssert.assertTableCount;
import static org.unitils.database.SqlUnitils.executeUpdate;
import static org.unitils.database.SqlUnitils.executeUpdateQuietly;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerExecuteUpdateAndCommitTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    private Mock<DefaultTransactionProvider> defaultTransactionProviderMock;
    private Mock<PlatformTransactionManager> platformTransactionManagerMock;
    @Dummy
    private TransactionStatus transactionStatus;
    @TestDataSource
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(defaultTransactionProviderMock.getMock());
        defaultTransactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager(null, dataSource);
        platformTransactionManagerMock.returns(transactionStatus).getTransaction(null);

        executeUpdate("create table my_table (id int)");
    }

    @After
    public void cleanup() {
        executeUpdateQuietly("drop table my_table");
    }


    @Test
    public void executeUpdateAndCommit() {
        dbMaintainSQLHandler.startTransaction(dataSource);

        dbMaintainSQLHandler.executeUpdateAndCommit("insert into my_table(id) values(10)", dataSource);

        assertTableCount(1, "my_table");
        platformTransactionManagerMock.assertInvoked().commit(transactionStatus);
    }

    @Test
    public void noCommitIfNoTransactionIsActive() {
        dbMaintainSQLHandler.executeUpdateAndCommit("insert into my_table(id) values(10)", dataSource);

        assertTableCount(1, "my_table");
        platformTransactionManagerMock.assertNotInvoked().commit(transactionStatus);
    }

    @Test
    public void exceptionWhenFailure() {
        try {
            dbMaintainSQLHandler.executeUpdateAndCommit("xx", dataSource);
            fail("DatabaseException expected");
        } catch (DatabaseException e) {
            assertEquals("Unable to perform database update:\n" +
                    "xx", e.getMessage());
            platformTransactionManagerMock.assertNotInvoked().commit(transactionStatus);
        }
    }
}
