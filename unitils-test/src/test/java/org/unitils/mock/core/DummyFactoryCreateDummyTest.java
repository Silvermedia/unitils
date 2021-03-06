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
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;
import org.unitils.mock.core.proxy.ProxyService;
import org.unitils.mock.core.proxy.impl.DummyProxyInvocationHandler;
import org.unitils.mock.mockbehavior.MockBehavior;
import org.unitils.mock.mockbehavior.MockBehaviorFactory;

import static org.junit.Assert.assertSame;
import static org.unitils.mock.ArgumentMatchers.notNull;

/**
 * @author Tim Ducheyne
 */
public class DummyFactoryCreateDummyTest extends UnitilsJUnit4 {

    private DummyFactory dummyFactory;

    private Mock<ProxyService> proxyServiceMock;
    private Mock<MockBehaviorFactory> mockBehaviorFactoryMock;
    @Dummy
    private MyInterface myInterfaceProxy;
    @Dummy
    private MockBehavior mockBehavior;


    @Before
    public void initialize() {
        dummyFactory = new DummyFactory(mockBehaviorFactoryMock.getMock(), proxyServiceMock.getMock());
    }


    @Test
    public void createDummy() {
        mockBehaviorFactoryMock.returns(mockBehavior).createDummyValueReturningMockBehavior(dummyFactory);
        proxyServiceMock.returns(myInterfaceProxy).createProxy(notNull(String.class), "myDummy", false, new DummyProxyInvocationHandler(mockBehavior), MyInterface.class);

        MyInterface result = dummyFactory.createDummy("myDummy", MyInterface.class);
        assertSame(myInterfaceProxy, result);
    }

    @Test
    public void defaultName() {
        mockBehaviorFactoryMock.returns(mockBehavior).createDummyValueReturningMockBehavior(dummyFactory);
        proxyServiceMock.returns(myInterfaceProxy).createProxy(notNull(String.class), "myInterface", false, new DummyProxyInvocationHandler(mockBehavior), MyInterface.class);

        MyInterface result = dummyFactory.createDummy(null, MyInterface.class);
        assertSame(myInterfaceProxy, result);
    }


    private static interface MyInterface {

        int test();
    }
}
