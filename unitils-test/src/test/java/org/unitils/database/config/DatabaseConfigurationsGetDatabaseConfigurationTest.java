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

package org.unitils.database.config;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.annotation.Dummy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class DatabaseConfigurationsGetDatabaseConfigurationTest extends UnitilsJUnit4 {

    /* Tested object */
    private DatabaseConfigurations databaseConfigurations;

    @Dummy
    private DatabaseConfiguration defaultDatabaseConfiguration;
    @Dummy
    private DatabaseConfiguration databaseConfigurationA;
    @Dummy
    private DatabaseConfiguration databaseConfigurationB;


    @Before
    public void initialize() {
        Map<String, DatabaseConfiguration> map = new HashMap<String, DatabaseConfiguration>();
        map.put("a", databaseConfigurationA);
        map.put("b", databaseConfigurationB);

        databaseConfigurations = new DatabaseConfigurations(defaultDatabaseConfiguration, map);
    }


    @Test
    public void found() {
        DatabaseConfiguration result = databaseConfigurations.getDatabaseConfiguration("a");
        assertSame(databaseConfigurationA, result);
    }

    @Test
    public void exceptionWhenNotFound() {
        try {
            databaseConfigurations.getDatabaseConfiguration("xxx");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("No configuration found for database with name 'xxx'", e.getMessage());
        }
    }

    @Test
    public void defaultWhenNoDatabaseName() {
        DatabaseConfiguration result = databaseConfigurations.getDatabaseConfiguration();
        assertSame(defaultDatabaseConfiguration, result);
    }

    @Test
    public void defaultWhenNullDatabaseName() {
        DatabaseConfiguration result = databaseConfigurations.getDatabaseConfiguration(null);
        assertSame(defaultDatabaseConfiguration, result);
    }

    @Test
    public void defaultWhenEmptyDatabaseName() {
        DatabaseConfiguration result = databaseConfigurations.getDatabaseConfiguration(null);
        assertSame(defaultDatabaseConfiguration, result);
    }
}
