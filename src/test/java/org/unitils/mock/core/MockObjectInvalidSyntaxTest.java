/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.unitils.core.UnitilsException;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.proxy.ProxyInvocation;

/**
 * todo javadoc
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class MockObjectInvalidSyntaxTest {

    /* Class under test */
    private MockObject<TestClass> mockObject1, mockObject2;


    @Before
    public void setUp() {
        Scenario scenario = new Scenario();
        mockObject1 = new MockObject<TestClass>("testMock1", TestClass.class, false, scenario);
        mockObject2 = new MockObject<TestClass>("testMock2", TestClass.class, false, scenario);
    }


    @Test
    public void incompleteBehaviorDefinition_returns() {
        mockObject1.returns("aValue");
        try {
            mockObject2.returns("aValue");
            fail("Expected exception");
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.returns(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteBehaviorDefinition_raises() {
        mockObject1.raises(new RuntimeException());
        try {
            mockObject2.returns("aValue");
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.raises(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteBehaviorDefinition_performs() {
        mockObject1.performs(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        try {
            mockObject2.assertInvoked();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.performs(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteBehaviorDefinition_onceReturns() {
        mockObject1.onceReturns("aValue");
        try {
            mockObject2.returns("aValue");
            fail("Expected exception");
        } catch (UnitilsException e) {
            e.printStackTrace();
            assertMessageContains(e.getMessage(), "testMock1.onceReturns(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteBehaviorDefinition_onceRaises() {
        mockObject1.onceRaises(new RuntimeException());
        try {
            mockObject2.returns("aValue");
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.onceRaises(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteBehaviorDefinition_oncePerforms() {
        mockObject1.oncePerforms(new MockBehavior() {
            public Object execute(ProxyInvocation mockInvocation) throws Throwable {
                return null;
            }
        });
        try {
            mockObject2.assertInvoked();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.oncePerforms(...) must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteAssertStatement_assertInvoked() {
        mockObject1.assertInvoked();
        try {
            mockObject2.assertInvoked();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.assertInvoked() must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteAssertStatement_assertInvokedInOrder() {
        mockObject1.assertInvokedInOrder();
        try {
            mockObject2.assertInvoked();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.assertInvokedInOrder() must be followed by a method invocation on the returned proxy");
        }
    }

    @Test
    public void incompleteAssertStatement_assertNotInvoked() {
        mockObject1.assertNotInvoked();
        try {
            mockObject2.assertNotInvoked();
        } catch (UnitilsException e) {
            assertMessageContains(e.getMessage(), "testMock1.assertNotInvoked() must be followed by a method invocation on the returned proxy");
        }
    }

    private void assertMessageContains(String message, String... subStrings) {
        for (String subString : subStrings) {
            assertTrue("Expected message to contain substring " + subString + ", but it doesn't.\nMessage was: " + message,
                    message.contains(subString));
        }
    }

    /**
     * Interface that is mocked during the tests
     */
    private static interface TestClass {

        public String testMethodReturningString();

        public void testMethod();

    }
}