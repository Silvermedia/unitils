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
import org.unitils.core.UnitilsException;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperGetSingleGenericClassTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type classType;
    private Type singleGenericType;
    private Type wildCardType;
    private Type multiGenericType;
    private Type nestedGenericType;
    private Type noTypeParametersType;


    @Before
    public void initialize() throws Exception {
        classType = StringBuilder.class;
        singleGenericType = MyClass.class.getDeclaredField("field1").getGenericType();
        wildCardType = MyClass.class.getDeclaredField("field2").getGenericType();
        multiGenericType = MyClass.class.getDeclaredField("field3").getGenericType();
        nestedGenericType = MyClass.class.getDeclaredField("field4").getGenericType();
        noTypeParametersType = MyClass.class.getDeclaredField("field5").getGenericType();
    }


    @Test
    public void singleGenericType() {
        typeWrapper = new TypeWrapper(singleGenericType);

        Class<?> result = typeWrapper.getSingleGenericClass();
        assertEquals(String.class, result);
    }

    @Test
    public void rawTypeWhenNestedGenericType() {
        typeWrapper = new TypeWrapper(nestedGenericType);

        Class<?> result = typeWrapper.getSingleGenericClass();
        assertEquals(List.class, result);
    }

    @Test
    public void exceptionWhenClassType() {
        try {
            typeWrapper = new TypeWrapper(classType);
            typeWrapper.getSingleGenericClass();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: java.lang.StringBuilder.\n" +
                    "Type is not a generic type.", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenWildCardType() {
        try {
            typeWrapper = new TypeWrapper(wildCardType);
            typeWrapper.getSingleGenericClass();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic class for type: java.util.List<?>.\n" +
                    "Generic type is not a class: ?", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNoTypeParameters() {
        try {
            typeWrapper = new TypeWrapper(noTypeParametersType);
            typeWrapper.getSingleGenericClass();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: org.unitils.core.reflect.TypeWrapperGetSingleGenericClassTest.org.unitils.core.reflect.TypeWrapperGetSingleGenericClassTest$MyGenericClass<java.lang.String>.MyGenericInnerClass.\n" +
                    "Type is not a generic type.", e.getMessage());
        }
    }

    @Test
    public void exceptionMoreThanOneGenericType() {
        try {
            typeWrapper = new TypeWrapper(multiGenericType);
            typeWrapper.getSingleGenericClass();
            fail("UnitilsException expected");

        } catch (UnitilsException e) {
            assertEquals("Unable to determine unique generic type for type: java.util.Map<java.lang.String, java.lang.String>.\n" +
                    "The type declares more than one generic type: [class java.lang.String, class java.lang.String]", e.getMessage());
        }
    }


    private static class MyClass {

        private List<String> field1;
        private List<?> field2;
        private Map<String, String> field3;
        private List<List<String>> field4;
        private MyGenericClass<String>.MyGenericInnerClass field5;
    }

    private static class MyGenericClass<T> {
        public class MyGenericInnerClass {
        }
    }
}
