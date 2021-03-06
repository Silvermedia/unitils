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
import org.unitils.mock.core.util.StackTraceService;
import org.unitils.mock.report.ScenarioReport;
import org.unitils.mock.report.impl.ObservedInvocationsReport;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class MockServiceAssertNoMoreMockInvocationsTest extends UnitilsJUnit4 {

    private MockService mockService;

    private Mock<Scenario> scenarioMock;
    private Mock<ObservedInvocationsReport> observedInvocationsReportMock;
    private Mock<ScenarioReport> scenarioReportMock;
    private Mock<StackTraceService> stackTraceServiceMock;
    private Mock<Mock> mockMock;

    @Dummy
    private ObservedInvocation observedInvocation1;
    @Dummy
    private ObservedInvocation observedInvocation2;
    @Dummy
    private Object testObject;
    @Dummy
    private Object proxy;


    @Before
    public void initialize() {
        mockService = new MockService(scenarioMock.getMock(), observedInvocationsReportMock.getMock(), scenarioReportMock.getMock(), stackTraceServiceMock.getMock());

        scenarioMock.returns(testObject).getTestObject();
        scenarioReportMock.returns("scenario report").createReport();
        mockMock.returns(proxy).getMock();
    }


    @Test
    public void okWhenNoUnverifiedInvocations() {
        scenarioMock.onceReturnsAll().getUnverifiedInvocations();

        mockService.assertNoMoreMockInvocations(mockMock.getMock());
    }

    @Test
    public void exceptionWhenUnverifiedInvocations() {
        scenarioMock.returnsAll(observedInvocation1, observedInvocation2).getUnverifiedProxyInvocations(proxy);
        observedInvocationsReportMock.returns("observed report").createReport(asList(observedInvocation1, observedInvocation2), testObject);
        try {
            mockService.assertNoMoreMockInvocations(mockMock.getMock());
            fail("MockAssertionError expected");
        } catch (MockAssertionError e) {
            assertEquals("No more invocations expected for mock " + mockMock.getMock() + ", yet observed following calls:\n" +
                    "observed report\n" +
                    "scenario report", e.getMessage());
        }
    }
}
