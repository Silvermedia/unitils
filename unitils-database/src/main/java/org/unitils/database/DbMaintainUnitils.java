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

import org.unitils.core.Unitils;
import org.unitils.database.dbmaintain.DbMaintainWrapper;

/**
 * Class providing access to the functionality of the database module using static methods. Meant
 * to be used directly in unit tests.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class DbMaintainUnitils {

    /**
     * Determines whether the test database is outdated and, if that is the case, updates the database with the
     * latest changes. See {@link org.dbmaintain.DbMaintainer} for more information.
     *
     * @return true if an update was performed
     */
    public static boolean updateDatabase() {
        return getDbMaintainWrapper().updateDatabase();
    }

    /**
     * Updates the database version to the current version, without issuing any other updates to the database.
     * This method can be used for example after you've manually brought the database to the latest version, but
     * the database version is not yet set to the current one. This method can also be useful for example for
     * reinitializing the database after having reorganized the scripts folder.
     */
    public static void markDatabaseAsUpToDate() {
        getDbMaintainWrapper().markDatabaseAsUpToDate();
    }

    /**
     * Clears all configured schema's. I.e. drops all tables, views and other database objects.
     */
    public static void clearDatabase() {
        getDbMaintainWrapper().clearDatabase();
    }

    /**
     * Cleans all configured schema's. I.e. removes all data from its database tables.
     */
    public static void cleanDatabase() {
        getDbMaintainWrapper().cleanDatabase();
    }

    /**
     * Disables all foreign key and not-null constraints on the configured schema's.
     */
    public static void disableConstraints() {
        getDbMaintainWrapper().disableConstraints();
    }

    /**
     * Updates all sequences that have a value below a the configured value to become equal to that value.
     */
    public static void updateSequences() {
        getDbMaintainWrapper().updateSequences();
    }


    protected static DbMaintainWrapper getDbMaintainWrapper() {
        return Unitils.getInstanceOfType(DbMaintainWrapper.class);
    }
}
