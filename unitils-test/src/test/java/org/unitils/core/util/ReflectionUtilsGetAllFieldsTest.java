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

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.util.CollectionUtils.asSet;

/**
 * @author Tim Ducheyne
 */
public class ReflectionUtilsGetAllFieldsTest {

    private Field field1;
    private Field field2;
    private Field field3;


    @Before
    public void initialize() throws Exception {
        field1 = SuperClass.class.getDeclaredField("field1");
        field2 = TestClass.class.getDeclaredField("field2");
        field3 = TestClass.class.getDeclaredField("field3");
    }


    @Test
    public void getClassWithName() {
        Set<Field> result = ReflectionUtils.getAllFields(TestClass.class);
        assertEquals(asSet(field3, field2, field1), result);
    }

    @Test
    public void emptyWhenNullClass() {
        Set<Field> result = ReflectionUtils.getAllFields(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void emptyWhenObjectClass() {
        Set<Field> result = ReflectionUtils.getAllFields(Object.class);
        assertTrue(result.isEmpty());
    }


    private static class SuperClass {

        private String field1;
    }

    private static class TestClass extends SuperClass {

        private int field2;
        private String field3;
    }
}