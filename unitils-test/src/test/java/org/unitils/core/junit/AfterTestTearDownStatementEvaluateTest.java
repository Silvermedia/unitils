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
package org.unitils.core.junit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.Statement;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.engine.UnitilsTestListener;
import org.unitils.mock.Mock;

/**
 * @author Tim Ducheyne
 */
public class AfterTestTearDownStatementEvaluateTest extends UnitilsJUnit4 {

    private AfterTestTearDownStatement afterTestTearDownStatement;

    private Mock<UnitilsTestListener> unitilsTestListenerMock;
    private Mock<Statement> statementMock;


    @Before
    public void initialize() {
        afterTestTearDownStatement = new AfterTestTearDownStatement(unitilsTestListenerMock.getMock(), statementMock.getMock());
    }


    @Test
    public void evaluate() throws Throwable {
        afterTestTearDownStatement.evaluate();
        statementMock.assertInvokedInSequence().evaluate();
        unitilsTestListenerMock.assertInvokedInSequence().afterTestTearDown();
    }
}
