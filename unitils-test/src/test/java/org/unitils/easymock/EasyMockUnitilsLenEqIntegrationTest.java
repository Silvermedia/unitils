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
package org.unitils.easymock;

import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.easymock.annotation.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.unitils.easymock.EasyMockUnitils.*;

/**
 * @author Tim Ducheyne
 */
public class EasyMockUnitilsLenEqIntegrationTest extends UnitilsJUnit4 {

    @Mock
    private MyInterface myInterfaceMock;


    @Test
    public void equalValues() {
        expect(myInterfaceMock.method(lenEq("value1"), lenEq(asList(1, 2, 3)), lenEq(new MyObject("value2", 5)))).andReturn("result");

        replay();
        String result = myInterfaceMock.method("value1", asList(1, 2, 3), new MyObject("value2", 5));
        assertEquals("result", result);
    }

    @Test
    public void noMatchWhenDifferentValues() {
        expect(myInterfaceMock.method(lenEq("value1"), lenEq(asList(1, 2, 3)), (MyObject) lenEq(null))).andReturn("result");

        replay();
        try {
            myInterfaceMock.method("xxx", asList(1, 2, 3), null);
            fail("AssertionError expected");
        } catch (AssertionError e) {
            assertEquals("\n" +
                    "  Unexpected method call method(\"xxx\", [1, 2, 3], null):\n" +
                    "    method(\"value1\", [1, 2, 3], null): expected: 1, actual: 0", e.getMessage());
            clearMocks();
        }
    }

    @Test
    public void orderIsIgnored() {
        expect(myInterfaceMock.method(lenEq("value1"), lenEq(asList(3, 2, 1)), lenEq(new MyObject("value2", 5)))).andReturn("result");

        replay();
        String result = myInterfaceMock.method("value1", asList(1, 2, 3), new MyObject("value2", 5));
        assertEquals("result", result);
    }

    @Test
    public void defaultsAreIgnored() {
        expect(myInterfaceMock.method((String) lenEq(null), (List<Integer>) lenEq(null), lenEq(new MyObject(null, 0)))).andReturn("result");

        replay();
        String result = myInterfaceMock.method("value1", asList(1, 2, 3), new MyObject("value2", 5));
        assertEquals("result", result);
    }

    @Test
    public void exceptionWhenNotAllArgumentMatchers() {
        try {
            expect(myInterfaceMock.method("value", asList(1, 2, 3), lenEq(new MyObject(null, 0)))).andReturn("result");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("This mocks control does not support mixing of no-argument matchers and per-argument matchers.\n" +
                    "Either no matchers are defined (the reflection argument matcher is then used by default) or all matchers are defined explicitly (Eg by using refEq()).", e.getMessage());
            clearMocks();
        }
    }


    public static interface MyInterface {

        String method(String arg1, List<Integer> arg2, MyObject arg3);
    }

    public static class MyObject {
        private String value1;
        private int value2;

        public MyObject(String value1, int value2) {
            this.value1 = value1;
            this.value2 = value2;
        }
    }
}
