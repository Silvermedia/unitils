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

package org.unitils.core.context;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.core.config.Configuration;
import org.unitils.mock.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class ContextGetInstanceOfTypeCyclicDependencyTest extends UnitilsJUnit4 {

    /* Tested object */
    private Context context;

    private Mock<Configuration> configurationMock;


    @Before
    public void initialize() {
        context = new Context(configurationMock.getMock());
    }


    @Test
    public void cyclicDependency() {
        try {
            context.getInstanceOfType(TestClassA.class);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to create instance for type org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassA.\n" +
                    "Reason: Unable to create instance for type org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassB.\n" +
                    "Reason: Unable to create instance for type org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassC.\n" +
                    "Reason: Unable to create instance for type org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassA: cyclic dependency detected between following types:\n" +
                    "org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassA\n" +
                    "org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassB\n" +
                    "org.unitils.core.context.ContextGetInstanceOfTypeCyclicDependencyTest$TestClassC\n", e.getMessage());
        }
    }


    protected static interface TestInterface {
    }

    protected static class TestClassA {

        public TestClassA(TestClassB testClassB) {
        }
    }

    protected static class TestClassB {

        public TestClassB(TestClassC testClassC) {
        }
    }

    protected static class TestClassC {

        public TestClassC(TestClassA testClassA) {
        }
    }
}
