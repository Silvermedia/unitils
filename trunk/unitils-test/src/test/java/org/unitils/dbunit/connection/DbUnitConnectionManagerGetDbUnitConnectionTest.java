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
package org.unitils.dbunit.connection;

import org.dbmaintain.database.Database;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.junit.Before;
import org.junit.Test;
import org.unitils.database.dbmaintain.DbMaintainWrapper;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitilsnew.UnitilsJUnit4;
import org.unitilsnew.core.context.Context;

import static org.dbunit.database.DatabaseConfig.*;
import static org.junit.Assert.*;
import static org.unitils.mock.ArgumentMatchers.isNull;

/**
 * @author Tim Ducheyne
 */
public class DbUnitConnectionManagerGetDbUnitConnectionTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbUnitConnectionManager dbUnitConnectionManager;

    private Mock<Context> contextMock;
    private Mock<DbMaintainWrapper> dbMaintainWrapperMock;
    private Mock<Database> databaseMock;
    @Dummy
    private IDataTypeFactory dataTypeFactory;


    @Before
    public void initialize() throws Exception {
        dbUnitConnectionManager = new DbUnitConnectionManager(contextMock.getMock(), dbMaintainWrapperMock.getMock());

        dbMaintainWrapperMock.returns(databaseMock).getDatabase(isNull(String.class));
        databaseMock.returns("quote").getIdentifierQuoteString();
        databaseMock.returns("dialect").getDatabaseInfo().getDialect();
        databaseMock.returns("SCHEMA").toCorrectCaseIdentifier("schema");
        contextMock.returns(dataTypeFactory).getInstanceOfType(IDataTypeFactory.class, "dialect");
    }


    @Test
    public void getDbUnitConnection() throws Exception {
        DbUnitConnection result = dbUnitConnectionManager.getDbUnitConnection("schema");
        assertEquals("SCHEMA", result.getSchema());
        DatabaseConfig config = result.getConfig();
        assertSame(dataTypeFactory, config.getProperty(PROPERTY_DATATYPE_FACTORY));
        assertEquals("quote?quote", config.getProperty(PROPERTY_ESCAPE_PATTERN));
        assertEquals(true, config.getProperty(FEATURE_BATCHED_STATEMENTS));
        assertEquals(true, config.getProperty(FEATURE_SKIP_ORACLE_RECYCLEBIN_TABLES));
    }

    @Test
    public void sameDbUnitConnectionIsReturnedForSameSchema() throws Exception {
        DbUnitConnection result1 = dbUnitConnectionManager.getDbUnitConnection("schema");
        DbUnitConnection result2 = dbUnitConnectionManager.getDbUnitConnection("schema");
        assertSame(result1, result2);
    }

    @Test
    public void differentDbUnitConnectionIsReturnedForDifferentSchema() throws Exception {
        DbUnitConnection result1 = dbUnitConnectionManager.getDbUnitConnection("schema1");
        DbUnitConnection result2 = dbUnitConnectionManager.getDbUnitConnection("schema2");
        assertNotSame(result1, result2);
    }
}
