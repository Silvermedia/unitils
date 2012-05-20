/*
 * Copyright 2012,  Unitils.org
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

package org.unitilsnew.database.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.database.config.DatabaseConfiguration;
import org.unitilsnew.database.dbmaintain.DbMaintainWrapper;

import javax.sql.DataSource;

import static org.junit.Assert.assertSame;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DataSourceServiceGetDataSourceWrapperTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceService dataSourceService;

    private Mock<DataSourceWrapperManager> dataSourceWrapperManagerMock;
    private Mock<DbMaintainWrapper> dbMaintainWrapperMock;
    private Mock<TransactionManager> transactionManagerMock;

    @Dummy
    private DatabaseConfiguration databaseConfiguration;
    private Mock<DataSourceWrapper> dataSourceWrapperMock;
    @Dummy
    private DataSource dataSource;


    @Before
    public void initialize() {
        dataSourceService = new DataSourceService(dataSourceWrapperManagerMock.getMock(), dbMaintainWrapperMock.getMock(), transactionManagerMock.getMock());
    }


    @Test
    public void getDataSourceWrapper() throws Exception {
        dataSourceWrapperManagerMock.returns(dataSourceWrapperMock).getDataSourceWrapper("databaseName");
        dataSourceWrapperMock.returns(dataSource).getWrappedDataSource();

        DataSourceWrapper result = dataSourceService.getDataSourceWrapper("databaseName");

        assertSame(dataSourceWrapperMock.getMock(), result);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
        dbMaintainWrapperMock.assertInvoked().updateDatabaseIfNeeded();
    }

    @Test
    public void nullDatabaseName() throws Exception {
        dataSourceWrapperManagerMock.returns(dataSourceWrapperMock).getDataSourceWrapper(isNull(String.class));
        dataSourceWrapperMock.returns(dataSource).getWrappedDataSource();

        DataSourceWrapper result = dataSourceService.getDataSourceWrapper(null);

        assertSame(dataSourceWrapperMock.getMock(), result);
        transactionManagerMock.assertInvoked().registerDataSource(dataSource);
        dbMaintainWrapperMock.assertInvoked().updateDatabaseIfNeeded();
    }
}
