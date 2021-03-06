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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Tim Ducheyne
 */
public class FieldWrapperGetTypeWrapperTest {

    /* Tested object */
    private FieldWrapper fieldWrapper;

    private Field field;
    private Field genericField;


    @Before
    public void initialize() throws Exception {
        field = MyClass.class.getDeclaredField("field");
        genericField = MyClass.class.getDeclaredField("genericField");
    }


    @Test
    public void classType() {
        fieldWrapper = new FieldWrapper(field);

        TypeWrapper result = fieldWrapper.getTypeWrapper();
        assertEquals(String.class, result.getWrappedType());
    }

    @Test
    public void genericType() {
        fieldWrapper = new FieldWrapper(genericField);

        TypeWrapper result = fieldWrapper.getTypeWrapper();
        assertEquals(genericField.getGenericType(), result.getWrappedType());
    }


    private static class MyClass {

        private String field;
        private List<String> genericField;
    }
}
