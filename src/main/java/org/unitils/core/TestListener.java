/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.core;

import java.lang.reflect.Method;

/**
 * Listener for test events. The events must follow following ordering:
 * <ul>
 * <li>[Unitils] beforeAll</li>
 * <li>[Unitils] beforeTestClass   - TestClass1</li>
 * <li>[Test]    testBeforeClass   - TestClass1  (not for JUnit3)</li>
 * <li>[Unitils] beforeTestSetUp   - TestClass1</li>
 * <li>[Test]    testSetUp         - TestClass1</li>
 * <li>[Unitils] beforeTestMethod  - TestClass1 - test1</li>
 * <li>[Test]    testMethod        - TestClass1 - test1</li>
 * <li>[Unitils] afterTestMethod   - TestClass1 - test1</li>
 * <li>[Test]    testTearDown      - TestClass1</li>
 * <li>[Unitils] afterTestTearDown - TestClass1</li>
 * <li>[Unitils] beforeTestSetUp   - TestClass1</li>
 * <li>[Test]    testSetUp         - TestClass1</li>
 * <li>[Unitils] beforeTestMethod  - TestClass1 - test2</li>
 * <li>[Test]    testMethod        - TestClass1 - test2</li>
 * <li>[Unitils] afterTestMethod   - TestClass1 - test2</li>
 * <li>[Test]    testTearDown      - TestClass1</li>
 * <li>[Unitils] afterTestTearDown - TestClass1</li>
 * <li>[Test]    testAfterClass    - TestClass1 (not for JUnit3)</li>
 * <li>[Unitils] afterTestClass    - TestClass1</li>
 * <li>[Unitils] afterAll</li>
 * </ul>
 * <p/>
 * The after methods will always when the before counterpart has run (or begun). For example if an exception occurs during
 * the beforeTestClass method, the afterTestClass will still be called.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class TestListener {


    /**
     * Invoked before any of the tests are run.
     * This can be overriden to for example add general initialisation.
     */
    public void beforeAll() {
        // empty
    }


    /**
     * Invoked before any of the test in a test class are run.
     * This can be overriden to for example add test-class initialisation.
     *
     * @param testClass The test class, not null
     */
    public void beforeTestClass(Class<?> testClass) {
        // empty
    }


    /**
     * Invoked before the test setup (eg @Before) is run.
     * This can be overriden to for example initialize the test-fixture.
     *
     * @param testObject The test instance, not null
     */
    public void beforeTestSetUp(Object testObject) {
        // empty
    }


    /**
     * Invoked before the test but after the test setup (eg @Before) is run.
     * This can be overriden to for example further initialize the test-fixture using values that were set during
     * the test setup.
     *
     * @param testObject The test instance, not null
     * @param testMethod The test method, not null
     */
    public void beforeTestMethod(Object testObject, Method testMethod) {
        // empty
    }


    /**
     * Invoked after the test run but before the test tear down (eg @After).
     * This can be overriden to for example add assertions for testing the result of the test.
     * It the before method or the test raised an exception, this exception will be passed to the method.
     *
     * @param testObject    The test instance, not null
     * @param testMethod    The test method, not null
     * @param testThrowable The throwable thrown during the test or beforeTestMethod, null if none was thrown
     */
    public void afterTestMethod(Object testObject, Method testMethod, Throwable testThrowable) {
        // empty
    }


    /**
     * Invoked after the test tear down (eg @After).
     * This can be overriden to for example peform extra cleanup after the test.
     *
     * @param testObject The test instance, not null
     */
    public void afterTestTearDown(Object testObject) {
        // empty
    }


    /**
     * Invoked after all tests of a test class have run.
     * This can be overriden to for example peform extra cleanup after the test.
     *
     * @param testClass The test class, not null
     */
    public void afterTestClass(Class<?> testClass) {
        // empty
    }


    /**
     * Invoked after all of the tests have run.
     * This can be overriden to for example add general finalisation.
     */
    public void afterAll() {
        // empty
    }

}
