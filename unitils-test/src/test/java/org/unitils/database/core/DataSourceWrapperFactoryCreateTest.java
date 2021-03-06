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

package org.unitils.database.core;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.database.config.DatabaseConfiguration;

import javax.sql.DataSource;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperFactoryCreateTest extends UnitilsJUnit4 {

    /* Tested object */
    private DataSourceWrapperFactory dataSourceWrapperFactory = new DataSourceWrapperFactory();

    private DatabaseConfiguration databaseConfiguration;


    @Before
    public void initialize() {
        databaseConfiguration = new DatabaseConfiguration("name", "dialect", "driver", "url", "user", "pass", "schema", asList("schema"), false, false);
    }


    @Test
    public void create() throws Exception {
        DataSourceWrapper result = dataSourceWrapperFactory.create(databaseConfiguration);

        assertSame(databaseConfiguration, result.getDatabaseConfiguration());
        DataSource dataSource = result.getDataSource(false);
        BasicDataSource basicDataSource = (BasicDataSource) dataSource;
        assertTrue(basicDataSource.isAccessToUnderlyingConnectionAllowed());
        assertEquals("driver", basicDataSource.getDriverClassName());
        assertEquals("user", basicDataSource.getUsername());
        assertEquals("pass", basicDataSource.getPassword());
        assertEquals("url", basicDataSource.getUrl());
    }
}
