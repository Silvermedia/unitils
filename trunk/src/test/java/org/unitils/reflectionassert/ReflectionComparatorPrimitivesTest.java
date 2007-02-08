/*
 * Copyright 2006 the original author or authors.
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

import junit.framework.TestCase;
import org.unitils.reflectionassert.ReflectionComparator.Difference;


/**
 * Test class for {@link ReflectionComparator}.
 * Contains tests with primitive types.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class ReflectionComparatorPrimitivesTest extends TestCase {

    /* Test object */
    private Primitives primitivesA;

    /* Same as A but different instance */
    private Primitives primitivesB;

    /* Same as A and B but different int value for intValue2 */
    private Primitives primitiveDifferentValue;

    /* Same as A and B but with 0 value for intValue2 */
    private Primitives primitives0Value;

    /* Test object with inner object */
    private Primitives primitivesInnerA;

    /* Same as innerA but different instance */
    private Primitives primitivesInnerB;

    /* Same as innerA and innerB but different int value for inner intValue2 */
    private Primitives primitivesInnerDifferentValue;

    /* Class under test */
    private ReflectionComparator reflectionComparator;


    /**
     * Initializes the test fixture.
     */
    protected void setUp() throws Exception {
        super.setUp();

        primitivesA = new Primitives(1, 2, null);
        primitivesB = new Primitives(1, 2, null);
        primitiveDifferentValue = new Primitives(1, 9999, null);
        primitives0Value = new Primitives(1, 0, null);

        primitivesInnerA = new Primitives(0, 0, primitivesA);
        primitivesInnerB = new Primitives(0, 0, primitivesB);
        primitivesInnerDifferentValue = new Primitives(0, 0, primitiveDifferentValue);

        reflectionComparator = new ReflectionComparator();
    }


    /**
     * Test for two equal primitives.
     */
    public void testGetDifference_equals() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitivesB);
        assertNull(result);
    }


    /**
     * Test for two equal autoboxing.
     * An autoboxed primitive should be considered equals to the object version.
     */
    @SuppressWarnings({"UnnecessaryBoxing"})
    public void testGetDifference_equalsAutoboxing() {
        Difference result = reflectionComparator.getDifference(5L, new Long(5));
        assertNull(result);
    }


    /**
     * Test for two equal primitives as an inner field of an object.
     */
    public void testGetDifference_equalsInner() {
        Difference result = reflectionComparator.getDifference(primitivesInnerA, primitivesInnerB);
        assertNull(result);
    }


    /**
     * Test for two equal primitives but of different type (int vs long).
     */
    public void testGetDifference_differentTypes() {
        Difference result = reflectionComparator.getDifference(5L, 5);
        assertNull(result);
    }


    /**
     * Test for two primitives that contain different values.
     */
    public void testGetDifference_notEqualsDifferentValues() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitiveDifferentValue);

        assertNotNull(result);
        assertEquals("intValue2", result.getFieldStack().get(0));
        assertEquals(2, result.getLeftValue());
        assertEquals(9999, result.getRightValue());
    }


    /**
     * Test for two primitives with right value 0.
     */
    public void testGetDifference_notEqualsRight0() {
        Difference result = reflectionComparator.getDifference(primitivesA, primitives0Value);

        assertNotNull(result);
        assertEquals("intValue2", result.getFieldStack().get(0));
        assertEquals(2, result.getLeftValue());
        assertEquals(0, result.getRightValue());
    }


    /**
     * Test for two primitives with left value 0.
     */
    public void testGetDifference_notEqualsLeft0() {
        Difference result = reflectionComparator.getDifference(primitives0Value, primitivesA);

        assertNotNull(result);
        assertEquals("intValue2", result.getFieldStack().get(0));
        assertEquals(0, result.getLeftValue());
        assertEquals(2, result.getRightValue());
    }


    /**
     * Test for objects with inner primitives that contain different values.
     */
    public void testGetDifference_notEqualsInnerDifferentValues() {
        Difference result = reflectionComparator.getDifference(primitivesInnerA, primitivesInnerDifferentValue);

        assertNotNull(result);
        assertEquals("inner", result.getFieldStack().get(0));
        assertEquals("intValue2", result.getFieldStack().get(1));
        assertEquals(2, result.getLeftValue());
        assertEquals(9999, result.getRightValue());
    }


    /**
     * Test class with failing equals.
     */
    private class Primitives {

        /* A fist int value */
        private int intValue1;

        /* A second int value */
        private int intValue2;

        /* An inner object */
        private Primitives inner;


        /**
         * Creates and initializes the element.
         *
         * @param intValue1 the first int value
         * @param intValue2 the second int value
         * @param inner     the inner collection
         */
        public Primitives(int intValue1, int intValue2, Primitives inner) {
            this.intValue1 = intValue1;
            this.intValue2 = intValue2;
            this.inner = inner;
        }

        /**
         * Gets the first int value
         *
         * @return the value
         */
        public int getIntValue1() {
            return intValue1;
        }

        /**
         * Gets the second int value
         *
         * @return the value
         */
        public int getIntValue2() {
            return intValue2;
        }

        /**
         * Gets the inner object
         *
         * @return the object
         */
        public Primitives getInner() {
            return inner;
        }

        /**
         * Always returns false
         *
         * @param o the object to compare to
         */
        public boolean equals(Object o) {
            return false;
        }
    }

}

