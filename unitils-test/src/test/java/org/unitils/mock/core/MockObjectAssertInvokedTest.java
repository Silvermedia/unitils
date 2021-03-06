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
package org.unitils.mock.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.matching.MatchingInvocationHandler;
import org.unitils.mock.core.matching.MatchingInvocationHandlerFactory;
import org.unitils.mock.core.proxy.impl.MatchingProxyInvocationHandler;

import static org.junit.Assert.*;

/**
 * @author Tim Ducheyne
 */
public class MockObjectAssertInvokedTest extends UnitilsJUnit4 {

    private MockObject<Object> mockObject;

    private Mock<MatchingInvocationHandlerFactory> matchingInvocationHandlerFactoryMock;
    private Mock<MatchingProxyInvocationHandler> matchingProxyInvocationHandlerMock;
    @Dummy
    private Object matchingProxy;
    @Dummy
    private MatchingInvocationHandler matchingInvocationHandler;


    @Before
    public void initialize() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, false, null,
                matchingProxyInvocationHandlerMock.getMock(), null, matchingInvocationHandlerFactoryMock.getMock(), null, null);
    }


    @Test
    public void assertInvoked() {
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createAssertInvokedVerifyingMatchingInvocationHandler();

        Object result = mockObject.assertInvoked();
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", true, matchingInvocationHandler);
    }

    @Test
    public void assertInvokedTimes() {
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createAssertInvokedVerifyingTimesMatchingInvocationHandler(5);

        Object result = mockObject.assertInvoked(5);
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", true, matchingInvocationHandler);
    }

    @Test
    public void exceptionWhenTimesIsNegative() {
        try {
            mockObject.assertInvoked(-5);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to assert that a method was invoked a number of times. Times must be a positive integer, but value is -5", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenTimesIsZero() {
        try {
            mockObject.assertInvoked(0);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to assert that a method was invoked a number of times. Times must be a positive integer, but value is 0", e.getMessage());
        }
    }

    @Test
    public void chainedAssertInvoked() {
        mockObject = new MockObject<Object>("name", Object.class, null, matchingProxy, true, null,
                matchingProxyInvocationHandlerMock.getMock(), null, matchingInvocationHandlerFactoryMock.getMock(), null, null);
        matchingInvocationHandlerFactoryMock.returns(matchingInvocationHandler).createAssertInvokedVerifyingMatchingInvocationHandler();

        Object result = mockObject.assertInvoked();
        assertSame(matchingProxy, result);
        matchingProxyInvocationHandlerMock.assertInvoked().startMatchingInvocation("name", false, matchingInvocationHandler);
    }
}
