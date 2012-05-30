/*
 * Copyright 2012,  Unitils.org
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

import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;
import org.unitils.mock.annotation.AfterCreateMock;
import org.unitils.mock.core.MockObject;
import org.unitils.mock.core.PartialMockObject;
import org.unitilsnew.core.TestField;
import org.unitilsnew.core.TestInstance;
import org.unitilsnew.core.TestListener;
import org.unitilsnew.core.TestPhase;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.unitils.util.AnnotationUtils.getMethodsAnnotatedWith;
import static org.unitils.util.ReflectionUtils.invokeMethod;
import static org.unitilsnew.core.TestPhase.CONSTRUCTION;

/**
 * @author Tim Ducheyne
 */
public class PartialMockTestListener extends TestListener {


    @Override
    public TestPhase getTestPhase() {
        return CONSTRUCTION;
    }


    @Override
    public void beforeTestSetUp(TestInstance testInstance) {
        List<TestField> partialMockTestFields = testInstance.getTestFieldsOfType(PartialMock.class);
        for (TestField partialMockTestField : partialMockTestFields) {
            Mock<?> mock = partialMockTestField.getValue();
            if (mock != null) {
                mock.resetBehavior();
                continue;
            }
            mock = createPartialMock(partialMockTestField, testInstance);
            partialMockTestField.setValue(mock);
        }
    }


    protected <T> Mock<T> createPartialMock(TestField testField, TestInstance testInstance) {
        String mockName = testField.getName();
        Class<?> mockedClass = getMockedClass(testField);
        Object testObject = testInstance.getTestObject();

        PartialMockObject<T> partialMock = new PartialMockObject<T>(mockName, mockedClass, testObject);
        callAfterCreateMockMethods(testObject, partialMock, mockName);
        return partialMock;
    }

    protected Class<?> getMockedClass(TestField testField) {
        try {
            return testField.getSingleGenericClass();
        } catch (UnitilsException e) {
            throw new UnitilsException("Unable to determine type of mock for field " + testField + ". A mock should be declared using the generic PartialMock<YourTypeToMock> type.", e);
        }
    }

    /**
     * todo switch to interface
     *
     * Calls all {@link org.unitils.mock.annotation.AfterCreateMock} annotated methods on the test, passing the given mock.
     * These annotated methods must have following signature <code>void myMethod(Object mock, String name, Class type)</code>.
     * If this is not the case, a runtime exception is called.
     *
     * @param testObject the test, not null
     * @param mockObject the mock, not null
     * @param name       the field(=mock) name, not null
     */
    protected void callAfterCreateMockMethods(Object testObject, Mock<?> mockObject, String name) {
        Set<Method> methods = getMethodsAnnotatedWith(testObject.getClass(), AfterCreateMock.class);
        for (Method method : methods) {
            try {
                invokeMethod(testObject, method, mockObject, name, ((MockObject<?>) mockObject).getMockedType());

            } catch (InvocationTargetException e) {
                throw new UnitilsException("An exception occurred while invoking an after create mock method.", e);
            } catch (Exception e) {
                throw new UnitilsException("Unable to invoke after create mock method. Ensure that this method has following signature: void myMethod(Object mock, String name, Class type)", e);
            }
        }
    }
}
