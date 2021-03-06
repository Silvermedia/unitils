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
package org.unitils.mock.listener;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.TestPhase;
import org.unitils.easymock.listener.MockFieldAnnotationListener;

import static org.junit.Assert.assertEquals;
import static org.unitils.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class MockFieldAnnotationListenerGetTestPhaseTest {

    private MockFieldAnnotationListener mockFieldAnnotationListener;


    @Before
    public void initialize() {
        mockFieldAnnotationListener = new MockFieldAnnotationListener(null);
    }


    @Test
    public void getTestPhase() {
        TestPhase result = mockFieldAnnotationListener.getTestPhase();
        assertEquals(CONSTRUCTION, result);
    }
}