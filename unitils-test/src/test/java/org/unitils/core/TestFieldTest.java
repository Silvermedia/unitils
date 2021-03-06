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

package org.unitils.core;

import org.junit.Before;
import org.junit.Test;
import org.unitils.core.reflect.ClassWrapper;
import org.unitils.core.reflect.FieldWrapper;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class TestFieldTest {

    /* Tested object */
    private TestField testField;

    private Field field;
    private MyClass testObject;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        testObject = new MyClass();

        testField = new TestField(new FieldWrapper(field), testObject);
    }


    @Test
    public void getField() {
        Field result = testField.getField();
        assertSame(field, result);
    }

    @Test
    public void getTestObject() {
        Object result = testField.getTestObject();
        assertSame(testObject, result);
    }

    @Test
    public void getName() {
        String result = testField.getName();
        assertEquals("field", result);
    }

    @Test
    public void getType() {
        assertEquals(String.class, testField.getType());
    }

    @Test
    public void nameAsToString() {
        String result = testField.toString();
        assertEquals("field", result);
    }

    @Test
    public void getClassWrapper() {
        ClassWrapper result = testField.getClassWrapper();
        assertEquals(String.class, result.getWrappedClass());
    }


    private static class MyClass {

        private String field;
    }
}
