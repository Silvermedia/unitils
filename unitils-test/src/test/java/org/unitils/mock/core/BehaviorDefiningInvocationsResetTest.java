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

import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class BehaviorDefiningInvocationsResetTest extends UnitilsJUnit4 {

    private BehaviorDefiningInvocations behaviorDefiningInvocations;

    @Dummy
    private BehaviorDefiningInvocation behaviorDefiningInvocation1;
    @Dummy
    private BehaviorDefiningInvocation behaviorDefiningInvocation2;


    @Before
    public void initialize() {
        behaviorDefiningInvocations = new BehaviorDefiningInvocations();
    }


    @Test
    public void reset() {
        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocation1);
        behaviorDefiningInvocations.addBehaviorDefiningInvocation(behaviorDefiningInvocation2);

        behaviorDefiningInvocations.reset();
        assertTrue(behaviorDefiningInvocations.getBehaviorDefiningInvocations().isEmpty());
    }
}
