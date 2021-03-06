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
import org.unitils.core.reflect.FieldWrapper;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Tim Ducheyne
 */
public class TestFieldGetValueTest {

    /* Tested object */
    private TestField testField;

    private FieldWrapper field;
    private MyClass testObject;


    @Before
    public void initialize() throws Exception {
        field = new FieldWrapper(MyClass.class.getDeclaredField("field"));
        testObject = new MyClass();

        testField = new TestField(field, testObject);
    }


    @Test
    public void getValue() {
        String result = testField.getValue();
        assertEquals("value", result);
    }

    @Test
    public void nullValue() {
        testObject.field = null;

        String result = testField.getValue();
        assertNull(result);
    }

    @Test(expected = ClassCastException.class)
    public void invalidType() {
        Map result = testField.getValue();
    }

    @Test(expected = UnitilsException.class)
    public void exception() {
        testField = new TestField(field, null);
        testField.getValue();
    }


    private static class MyClass {

        private String field = "value";
    }
}
