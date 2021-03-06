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
package org.unitils.mock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class MockMatchingBehaviorIntegrationTest extends UnitilsJUnit4 {

    private Mock<TestInterface> mockObject;


    @Test
    public void betterMatchBySpecifyingValue() {
        mockObject.returns(1).testMethod1(null, null, null);
        mockObject.returns(2).testMethod1("arg1", null, null);

        int result = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void betterMatchBySpecifyingValue_oneTimeMatching() {
        mockObject.onceReturns(1).testMethod1(null, null, null);
        mockObject.onceReturns(2).testMethod1("arg1", null, null);

        int result = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void lastAlwaysMatchOverridesPreviousWhenMultipleBestMatches() {
        mockObject.returns(1).testMethod1("arg1", null, null);
        mockObject.returns(2).testMethod1("arg1", null, null);

        int result1 = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        int result2 = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(2, result1);
        assertEquals(2, result2);
    }

    @Test
    public void oneTimeMatchersKeepSequence() {
        mockObject.onceReturns(1).testMethod1("arg1", "arg2", null);
        mockObject.onceReturns(2).testMethod1("arg1", "arg2", null);

        int result1 = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        int result2 = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(1, result1);
        assertEquals(2, result2);
    }

    @Test
    public void betterMatchIfMoreSpecific() {
        mockObject.returns(1).testMethod1("arg1", null, null);
        mockObject.returns(2).testMethod1("arg1", "arg2", null);
        mockObject.onceReturns(3).testMethod1(null, "arg2", null);

        int result = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void oneTimeMatchingPrecedesWhenMultipleBestMatch() {
        mockObject.returns(1).testMethod1("arg1", "arg2", null);
        mockObject.onceReturns(2).testMethod1("arg1", "arg2", null);
        mockObject.returns(3).testMethod1("arg1", "arg2", null);

        int result = mockObject.getMock().testMethod1("arg1", "arg2", "arg3");
        assertEquals(2, result);
    }

    @Test
    public void noMatchFound() {
        mockObject.returns(1).testMethod1("arg1", null, null);
        mockObject.returns(2).testMethod1("arg1", "arg2", null);

        int result = mockObject.getMock().testMethod1("xxx", "arg2", "arg3");
        assertEquals(0, result);
    }

    @Test
    public void useIdentityWhenEqualObjects() {
        Value value1 = new Value();
        Value value2 = new Value();
        mockObject.returns(1).testMethod2(value1);
        mockObject.returns(2).testMethod2(value2);

        int result1 = mockObject.getMock().testMethod2(value1);
        assertEquals(1, result1);
        int result2 = mockObject.getMock().testMethod2(value2);
        assertEquals(2, result2);
    }

    @Test
    public void oneTimeMatchersOnlyMatchedOnce() {
        mockObject.onceReturns(1).testMethod3();
        mockObject.returns(2).testMethod3();

        int result1 = mockObject.getMock().testMethod3();
        int result2 = mockObject.getMock().testMethod3();
        int result3 = mockObject.getMock().testMethod3();
        assertEquals(1, result1);
        assertEquals(2, result2);
        assertEquals(2, result3);
    }


    private static interface TestInterface {

        int testMethod1(String arg1, String arg2, String arg3);

        int testMethod2(Value value);

        int testMethod3();
    }

    private static class Value {
        @Override
        public boolean equals(Object obj) {
            return true;
        }
    }
}