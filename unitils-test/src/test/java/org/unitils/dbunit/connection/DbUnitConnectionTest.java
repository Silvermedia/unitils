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

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.mock.Mock;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.unitils.mock.MockUnitils.assertNoMoreInvocations;

/**
 * @author Tim Ducheyne
 */
public class DbUnitConnectionTest extends UnitilsJUnit4 {

    /* Tested object */
    private DbUnitConnection dbUnitConnection;

    private Mock<DataSource> dataSourceMock;


    @Before
    public void initialize() {
        dbUnitConnection = new DbUnitConnection(dataSourceMock.getMock(), "schema");
    }


    @Test
    public void getSchemaName() {
        String result = dbUnitConnection.getSchema();
        assertEquals("schema", result);
    }

    @Test
    public void ignoreClose() throws Exception {
        dbUnitConnection.close();
        assertNoMoreInvocations();
    }
}
