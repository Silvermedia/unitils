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
package org.unitils.easymock.listener;

import org.unitils.core.TestInstance;
import org.unitils.core.TestListener;
import org.unitils.core.annotation.Property;
import org.unitils.easymock.core.MockService;

/**
 * @author Tim Ducheyne
 */
public class AutoVerifyTestListener extends TestListener {

    protected MockService mockService;
    /* Indicates whether verify() is automatically called on every mock object after each test method execution */
    protected boolean autoVerifyAfterTest;


    public AutoVerifyTestListener(MockService mockService, @Property("easymock.autoVerifyAfterTest") boolean autoVerifyAfterTest) {
        this.mockService = mockService;
        this.autoVerifyAfterTest = autoVerifyAfterTest;
    }


    @Override
    public void afterTestMethod(TestInstance testInstance, Throwable testThrowable) {
        if (autoVerifyAfterTest && testThrowable == null) {
            mockService.verify();
        }
        mockService.clearMocks();
    }
}
