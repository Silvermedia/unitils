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

import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.transaction.impl.DefaultTransactionProvider;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import javax.sql.DataSource;

/**
 * @author Tim Ducheyne
 */
public class DbMaintainSQLHandlerEndTransactionAndRollbackTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbMaintainSQLHandler dbMaintainSQLHandler;

    private Mock<DefaultTransactionProvider> defaultTransactionProviderMock;
    private Mock<PlatformTransactionManager> platformTransactionManagerMock;
    @Dummy
    private TransactionStatus transactionStatus;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dbMaintainSQLHandler = new DbMaintainSQLHandler(defaultTransactionProviderMock.getMock());

        defaultTransactionProviderMock.returns(platformTransactionManagerMock).getPlatformTransactionManager(null, dataSource);
        platformTransactionManagerMock.returns(transactionStatus).getTransaction(null);
    }


    @Test
    public void endTransactionAndRollback() {
        dbMaintainSQLHandler.startTransaction(dataSource);
        dbMaintainSQLHandler.endTransactionAndRollback(dataSource);

        platformTransactionManagerMock.assertInvoked().rollback(transactionStatus);
    }

    @Test
    public void ignoreWhenNoTransactionWasActive() {
        dbMaintainSQLHandler.endTransactionAndRollback(dataSource);

        platformTransactionManagerMock.assertNotInvoked().rollback(transactionStatus);
    }
}
