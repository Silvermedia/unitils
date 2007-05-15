/*
 * Copyright 2006 the original author or authors.
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
package org.unitils.dbmaintainer.structure;


import org.unitils.dbmaintainer.util.DatabaseTask;

/**
 * Defines the contract for implementations that disable all foreign key and not-null constraints on an database.
 * The implementation guarantees that, when the method disableConstraints() is is called once after each database update.
 *
 * @author Filip Neven
 * @author Bart Vermeiren
 * @author Tim Ducheyne
 */
public interface ConstraintsDisabler extends DatabaseTask {


    /**
     * Generates statements to disable the constraints of the associated database. This method does only guarantee that
     * constraints are fully disabled after the method enableConstraintsOnConnection has been called on the <code>Connection</code> object that is used.
     */
    void disableConstraints();


}
