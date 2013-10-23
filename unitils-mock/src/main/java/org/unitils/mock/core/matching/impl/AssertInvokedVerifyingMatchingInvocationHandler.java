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
package org.unitils.mock.core.matching.impl;

import org.unitils.mock.Mock;
import org.unitils.mock.core.MatchingInvocation;
import org.unitils.mock.core.MockFactory;
import org.unitils.mock.core.ObservedInvocation;
import org.unitils.mock.core.Scenario;
import org.unitils.mock.report.ScenarioReport;

import static org.unitils.util.ReflectionUtils.getSimpleMethodName;


/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class AssertInvokedVerifyingMatchingInvocationHandler extends AssertVerifyingMatchingInvocationHandler {

    protected Scenario scenario;


    public AssertInvokedVerifyingMatchingInvocationHandler(Scenario scenario, MockFactory mockFactory, ScenarioReport scenarioReport) {
        super(mockFactory, scenarioReport);
        this.scenario = scenario;
    }


    protected String performAssertion(MatchingInvocation matchingInvocation) {
        ObservedInvocation observedInvocation = scenario.verifyInvocation(matchingInvocation);
        if (observedInvocation == null) {
            return getAssertInvokedErrorMessage(matchingInvocation);
        }
        return null;
    }

    protected Object performChainedAssertion(Mock<?> mock) {
        return mock.assertInvoked();
    }


    protected String getAssertInvokedErrorMessage(MatchingInvocation matchingInvocation) {
        String simpleMethodName = getSimpleMethodName(matchingInvocation.getMethod());

        StringBuilder message = new StringBuilder();
        message.append("Expected invocation of ");
        message.append(simpleMethodName);
        message.append(", but it didn't occur.");
        return message.toString();
    }
}