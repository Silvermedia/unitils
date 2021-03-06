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
package org.unitils.core.util;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;

import java.lang.reflect.Field;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionUtilsGetFieldValueTest extends UnitilsJUnit4 {

    private TestClass testClass;
    private Field field;

    @Before
    public void initialize() throws Exception {
        field = TestClass.class.getDeclaredField("value");
        testClass = new TestClass("xxx");
    }


    @Test
    public void getFieldValue() {
        String result = ReflectionUtils.getFieldValue(testClass, field);
        assertEquals("xxx", result);
    }

    @Test
    public void exceptionWhenFieldNotFound() {
        try {
            ReflectionUtils.getFieldValue(new Properties(), field);
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Error while trying to access field private java.lang.String org.unitils.core.util.ReflectionUtilsGetFieldValueTest$TestClass.value\n" +
                    "Reason: IllegalArgumentException: Can not set java.lang.String field org.unitils.core.util.ReflectionUtilsGetFieldValueTest$TestClass.value to java.util.Properties", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWrongType() {
        try {
            Properties result = ReflectionUtils.getFieldValue(testClass, field);
            fail("ClassCastException expected");
        } catch (ClassCastException e) {
            assertEquals("java.lang.String cannot be cast to java.util.Properties", e.getMessage());
        }
    }


    public static class TestClass {

        private String value;

        public TestClass(String value) {
            this.value = value;
        }
    }
}