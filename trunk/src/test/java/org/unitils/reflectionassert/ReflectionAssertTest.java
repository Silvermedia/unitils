/*
 * Copyright 2008,  Unitils.org
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
package org.unitils.reflectionassert;

import junit.framework.AssertionFailedError;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertRefEquals;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;
import static org.unitils.util.CollectionUtils.asSet;

import static java.util.Arrays.asList;


/**
 * Test class for {@link ReflectionAssert}.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionAssertTest extends UnitilsJUnit4 {

    /* Test object */
    private TestObjectString testObjectAString;

    /* Same as A but different instance */
    private TestObjectString testObjectBString;

    /* Same as A and B but different string value for stringValue2 */
    private TestObjectString testObjectDifferentValueString;

    /* Test object */
    private TestObjectIntString testObjectAIntString;

    /* Same as A but different instance */
    private TestObjectIntString testObjectBIntString;


    /**
     * Initializes the test fixture.
     */
    @Before
    public void setUp() throws Exception {
        testObjectAString = new TestObjectString("test 1", "test 2");
        testObjectBString = new TestObjectString("test 1", "test 2");
        testObjectDifferentValueString = new TestObjectString("test 1", "XXXXXX");
        testObjectAIntString = new TestObjectIntString(1, "test");
        testObjectBIntString = new TestObjectIntString(1, "test");
    }


    /**
     * Test for two equal objects.
     */
    @Test
    public void testAssertRefEquals_equals() {
        assertRefEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects (message version).
     */
    @Test
    public void testAssertRefEquals_equalsMessage() {
        assertRefEquals("a message", testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects.
     */
    @Test
    public void testAssertLenEquals_equals() {
        assertLenEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for two equal objects (message version).
     */
    @Test
    public void testAssertLenEquals_equalsMessage() {
        assertLenEquals("a message", testObjectAString, testObjectBString);
    }


    /**
     * Test for two objects that contain different values.
     */
    @Test(expected = AssertionFailedError.class)
    public void testAssertRefEquals_notEqualsDifferentValues() {
        assertRefEquals(testObjectAString, testObjectDifferentValueString);
    }


    /**
     * Test case for a null left-argument.
     */
    @Test(expected = AssertionFailedError.class)
    public void testAssertRefEquals_leftNull() {
        assertRefEquals(null, testObjectAString);
    }


    /**
     * Test case for a null right-argument.
     */
    @Test(expected = AssertionFailedError.class)
    public void testAssertRefEquals_rightNull() {
        assertRefEquals(testObjectAString, null);
    }


    /**
     * Test case for both null arguments.
     */
    @Test
    public void testAssertRefEquals_null() {
        assertRefEquals(null, null);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertRefEquals_equalsLenientOrder() {
        assertRefEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"), LENIENT_ORDER);
    }


    /**
     * Test for two equal sets but with different order.
     */
    @Test
    public void testAssertRefEquals_equalsLenientOrderSet() {
        assertRefEquals(asSet(testObjectAString, testObjectAIntString), asSet(testObjectBIntString, testObjectBString), LENIENT_ORDER, IGNORE_DEFAULTS);
    }


    /**
     * Test for two equal collections but with different order.
     */
    @Test
    public void testAssertLenEquals_equalsLenientOrder() {
        assertLenEquals(asList("element1", "element2", "element3"), asList("element3", "element1", "element2"));
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertRefEquals_equalsIgnoredDefault() {
        testObjectAString.setString1(null);
        testObjectBString.setString1("xxxxxx");

        assertRefEquals(testObjectAString, testObjectBString, IGNORE_DEFAULTS);
    }


    /**
     * Test for ignored default left value.
     */
    @Test
    public void testAssertLenEquals_equalsIgnoredDefault() {
        testObjectAString.setString1(null);
        testObjectBString.setString1("xxxxxx");

        assertLenEquals(testObjectAString, testObjectBString);
    }


    /**
     * Test for message of 2 not equal arrays. Should return return actual content instead of something like String[234
     */
    @Test
    public void testAssertLenEquals_formatArraysMessage() {
        try {
            assertLenEquals(new String[]{"test1", "test2"}, new Integer[]{1, 2});
        } catch (AssertionFailedError a) {
            // expected
            assertTrue(a.getMessage().contains("[\"test1\", \"test2\"]"));
            assertTrue(a.getMessage().contains("[1, 2]"));
        }
    }


    /**
     * Test class with 2 string fields and a failing equals.
     */
    private class TestObjectString {

        private String string1;

        private String string2;

        public TestObjectString(String stringValue1, String stringValue2) {
            this.string1 = stringValue1;
            this.string2 = stringValue2;
        }

        public String getString1() {
            return string1;
        }

        public void setString1(String string1) {
            this.string1 = string1;
        }

        public String getString2() {
            return string2;
        }

        public void setString2(String string2) {
            this.string2 = string2;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        @Override
        public boolean equals(Object o) {
            return false;
        }
    }


    /**
     * Test class with int and string field.
     */
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private class TestObjectIntString {

        private int intValue;

        private String stringValue;

        public TestObjectIntString(int intValue, String stringValue) {
            this.intValue = intValue;
            this.stringValue = stringValue;
        }
    }

}