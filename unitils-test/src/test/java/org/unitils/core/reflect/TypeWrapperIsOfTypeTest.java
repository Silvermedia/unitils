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

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class TypeWrapperIsOfTypeTest {

    /* Tested object */
    private TypeWrapper typeWrapper;

    private Type classType1;
    private Type classType2;
    private Type genericType1;
    private Type genericType2;
    private Type otherGenericType;


    @Before
    public void initialize() throws Exception {
        classType1 = StringBuilder.class;
        classType2 = StringBuilder.class;
        genericType1 = MyClass.class.getDeclaredField("field1").getGenericType();
        genericType2 = MyClass.class.getDeclaredField("field2").getGenericType();
        otherGenericType = MyClass.class.getDeclaredField("field3").getGenericType();
    }


    @Test
    public void genericType() {
        typeWrapper = new TypeWrapper(genericType1);

        boolean result = typeWrapper.isOfType(genericType2);
        assertTrue(result);
    }

    @Test
    public void classType() {
        typeWrapper = new TypeWrapper(classType1);

        boolean result = typeWrapper.isOfType(classType2);
        assertTrue(result);
    }

    @Test
    public void falseWhenOtherType() {
        typeWrapper = new TypeWrapper(genericType1);

        boolean result = typeWrapper.isOfType(otherGenericType);
        assertFalse(result);
    }

    @Test
    public void falseWhenNullType() {
        typeWrapper = new TypeWrapper(genericType1);

        boolean result = typeWrapper.isOfType(null);
        assertFalse(result);
    }


    private static class MyClass {

        private List<String> field1;
        private List<String> field2;
        private Map<String, String> field3;
    }
}
