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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.unitils.core.Unitils;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.core.proxy.StackTraceService;
import org.unitils.mock.dummy.DummyObjectFactory;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockUnitils {

    /* The logger instance for this class */
    protected static Log logger = LogFactory.getLog(MockUnitils.class);

    // todo move to getter functions to avoid unnecessary inits
    protected static Scenario scenario = Unitils.getInstanceOfType(Scenario.class);
    protected static DummyObjectFactory dummyObjectFactory = Unitils.getInstanceOfType(DummyObjectFactory.class);
    protected static StackTraceService stackTraceService = Unitils.getInstanceOfType(StackTraceService.class);


    public static void assertNoMoreInvocations() {
        StackTraceElement[] invocationStackTrace = stackTraceService.getInvocationStackTrace(MockUnitils.class, false);
        scenario.assertNoMoreInvocations(invocationStackTrace);
    }

    // todo log error when mock chaining does not work  e.g.
    // proxyInvocationMock.returns(String.class).getMethod().getReturnType();

    // todo add createMocks method so that you no longer have to extend unitils base class

    public static <T> T createDummy(Class<T> type) {
        return dummyObjectFactory.createDummy(type);
    }


    public static void logFullScenarioReport() {
        logger.info("\n\n" + scenario.createFullReport());
    }

    public static void logObservedScenario() {
        logger.info("\n\nObserved scenario:\n\n" + scenario.createObservedInvocationsReport());
    }

    public static void logDetailedObservedScenario() {
        logger.info("\n\nDetailed observed scenario:\n\n" + scenario.createDetailedObservedInvocationsReport());
    }

    public static void logSuggestedAsserts() {
        logger.info("\n\nSuggested assert statements:\n\n" + scenario.createSuggestedAssertsReport());
    }
}
