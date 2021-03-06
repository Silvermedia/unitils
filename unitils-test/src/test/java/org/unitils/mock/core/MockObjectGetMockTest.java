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
import org.unitils.mock.annotation.Dummy;

import java.util.Properties;

import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class MockObjectGetMockTest extends UnitilsJUnit4 {

    private MockObject<Properties> mockObject;
    @Dummy
    private Properties proxy;


    @Before
    public void initialize() {
        mockObject = new MockObject<Properties>("name", null, proxy, null, false, null, null, null, null, null, null);
    }


    @Test
    public void getMock() {
        Properties result = mockObject.getMock();
        assertSame(proxy, result);
    }
}
