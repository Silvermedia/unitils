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
package org.unitils.easymock.core;

import org.easymock.internal.MocksControl;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.easymock.util.*;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MockServiceCreateMockTest extends UnitilsJUnit4 {

    private MockService mockService;

    private Mock<MocksControlFactory> mocksControlFactoryMock;
    private Mock<MocksControl> mocksControlMock;
    @Dummy
    private MyInterface mockObject;


    @Before
    public void initialize() throws Exception {
        mockService = new MockService(mocksControlFactoryMock.getMock());

        mocksControlFactoryMock.returns(mocksControlMock).createMocksControl(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.IGNORE_DEFAULTS);
        mocksControlMock.returns(mockObject).createMock(MyInterface.class);
    }


    @Test
    public void createMock() {
        MyInterface result = mockService.createMock(MyInterface.class, InvocationOrder.DEFAULT, Calls.DEFAULT, Order.DEFAULT, Dates.DEFAULT, Defaults.IGNORE_DEFAULTS);
        assertSame(mockObject, result);

        mockService.replay();
        mocksControlMock.assertInvoked().replay();
    }


    private static interface MyInterface {
    }
}
