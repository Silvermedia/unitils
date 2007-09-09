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

import org.unitils.core.UnitilsException;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;


/**
 * Abstract superclass that defines a template for sub implementations that can compare objects of a certain kind.
 * Different instances of different subtypes will be chained to obtain a reflection comparator chain. This chain
 * will compare two objects with eachother through reflection. Depending on the composition of the chain, a number
 * of 'leniency levels' are in operation.
 * <p/>
 * If the check indicates that both objects are not equal, the first (and only the first!) found difference is returned.
 * The actual difference can then be retrieved by the fieldStack, leftValue and rightValue properties.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
abstract public class ReflectionComparator {

    /**
     * Root of the comparator chain. Comparisons must start with calling the getDifference method of this object
     */
    protected ReflectionComparator rootComparator;

    /**
     * Next element in the comparator chain.
     */
    protected ReflectionComparator chainedComparator;


    /**
     * Constructs a new instance, with the given comparator as the next element in the chain. Makes sure that this
     * instance is registered as root comparator of the given chained comparator. Setting the root comparator gets
     * propagated to all elements in the chain. This way, all comparators share the same root at all times.
     *
     * @param chainedComparator The next comparator in the chain
     */
    public ReflectionComparator(ReflectionComparator chainedComparator) {
        this.chainedComparator = chainedComparator;
        setRootComparator(this);
    }


    /**
     * Sets the root comparator. This operation is propagated to all comparators in the chain. This way, all comparators
     * share the same root at all times.
     *
     * @param rootComparator The root comparator, i.e. the first comparator in the chain
     */
    protected void setRootComparator(ReflectionComparator rootComparator) {
        this.rootComparator = rootComparator;
        if (chainedComparator != null) {
            chainedComparator.setRootComparator(rootComparator);
        }
    }


    /**
     * Indicates whether this ReflectionComparator is able to check whether their is a difference in the given left
     * and right objects or not.
     *
     * @param left  The left object
     * @param right The right object
     * @return true if this ReflectionComparator is able to check whether their is a difference in the given left
     *         and right objects, false otherwise
     */
    abstract public boolean canHandle(Object left, Object right);


    /**
     * Checks whether there is a difference between the left and right objects. Whether there is a difference, depends
     * on the concrete comparators in the chain.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return the difference, null if there is no difference
     */
    public Difference getDifference(Object left, Object right) {
        return getDifference(left, right, new Stack<String>(), new HashSet<TraversedInstancePair>());
    }


    /**
     * If this ReflectionComparator is able to check whether their is a difference in the given left
     * and right objects (i.e. {@link #canHandle(Object,Object)} returns true), the objects are compared.
     *
     * @param left                   The left instance
     * @param right                  The right instance
     * @param fieldStack             Stack indicating the path from the root of the object structure to the object that is currently
     *                               compared
     * @param traversedInstancePairs Set with pairs of objects that have been compared with eachother. A pair of two
     * @return The difference, null if there is no difference
     */
    protected Difference getDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs) {
        if (isAlreadyTraversedInstancePair(left, right, traversedInstancePairs)) {
            return null;
        }

        if (canHandle(left, right)) {
            registerTraversedInstancePair(left, right, traversedInstancePairs);
            return doGetDifference(left, right, fieldStack, traversedInstancePairs);
        } else {
            if (chainedComparator == null) {
                throw new UnitilsException("No ReflectionComparator found for objects " + left + " and" + right + " at " + fieldStack.toString());
            } else {
                return chainedComparator.getDifference(left, right, fieldStack, traversedInstancePairs);
            }
        }
    }


    /**
     * Abstract method that makes up the core of a reflection comparator. Implementations should return a concrete
     * {@link Difference} object when left and right are different, or null otherwise. This method will only be called
     * if {@link #canHandle(Object,Object)} returns true. An implementation doesn't have to take care of chaining or
     * circular references.
     *
     * @param left                   The left instance
     * @param right                  The right instance
     * @param fieldStack             Stack indicating the path from the root of the object structure to the object that is currently
     *                               compared
     * @param traversedInstancePairs Set with pairs of objects that have been compared with eachother. A pair of two
     * @return The difference, null if there is no difference
     */
    abstract protected Difference doGetDifference(Object left, Object right, Stack<String> fieldStack, Set<TraversedInstancePair> traversedInstancePairs);


    /**
     * Checks whether there is no difference between the left and right objects. The meaning of no difference is
     * determined by the set comparator modes. See class javadoc for more info.
     *
     * @param left  the left instance
     * @param right the right instance
     * @return true if there is no difference, false otherwise
     */
    public boolean isEqual(Object left, Object right) {
        Difference difference = rootComparator.getDifference(left, right);
        return difference == null;
    }


    /**
     * Registers the fact that the given left and right object have been compared, to make sure the same two objects
     * will not be compared again (to avoid infinite loops in case of circular references)
     *
     * @param left                   the left instance
     * @param right                  the right instance
     * @param traversedInstancePairs Set with pairs of objects that have been compared with eachother. A pair of two
     *                               same objects will not be compared again, in order to avoid infinite loops
     */
    protected void registerTraversedInstancePair(Object left, Object right, Set<TraversedInstancePair> traversedInstancePairs) {
        if (left != null && right != null) {
            traversedInstancePairs.add(new TraversedInstancePair(left, right));
        }
    }


    /**
     * Checks whether the given left and right object have already been compared, according to the given set of
     * traversedInstancePairs.
     *
     * @param left                   the left instance
     * @param right                  the right instance
     * @param traversedInstancePairs Set with pairs of objects that have been compared with eachother. A pair of two
     *                               same objects will not be compared again, in order to avoid infinite loops
     * @return true if the given left and right object have already been compared
     */
    protected boolean isAlreadyTraversedInstancePair(Object left, Object right, Set<TraversedInstancePair> traversedInstancePairs) {
        if (left == null || right == null) {
            return false;
        } else {
            return traversedInstancePairs.contains(new TraversedInstancePair(left, right));
        }
    }


    /**
     * A class for holding the difference between two objects.
     */
    public static class Difference {

        /* A message describing the difference */
        protected String message;

        /* When isEquals is false this will contain the stack of the fieldnames where the difference was found. <br>
         * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA". */
        protected Stack<String> fieldStack;

        /* When isEquals is false this will contain the left value of the field where the difference was found. */
        protected Object leftValue;

        /* When isEquals is false, this will contain the right value of the field where the difference was found. */
        protected Object rightValue;


        /**
         * Creates a difference.
         *
         * @param message    a message describing the difference
         * @param leftValue  the left instance
         * @param rightValue the right instance
         * @param fieldStack the current field names
         */
        public Difference(String message, Object leftValue, Object rightValue, Stack<String> fieldStack) {
            this.message = message;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
            this.fieldStack = fieldStack;
        }

        /**
         * Gets a string representation of the field stack.
         * Eg primitiveFieldInB.fieldBinA.fieldA
         * The top-level element is an empty string.
         *
         * @return the field names as sting
         */
        public String getFieldStackAsString() {
            String result = "";
            Iterator<?> iterator = fieldStack.iterator();
            while (iterator.hasNext()) {
                result += iterator.next();
                if (iterator.hasNext()) {
                    result += ".";
                }
            }
            return result;
        }


        /**
         * Gets the message indicating the kind of difference.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }


        /**
         * Gets the stack of the fieldnames where the difference was found.
         * The inner most field will be the top of the stack, eg "primitiveFieldInB", "fieldBinA", "fieldA".
         * The top-level element has an empty stack.
         *
         * @return the stack of field names, not null
         */
        public Stack<String> getFieldStack() {
            return fieldStack;
        }


        /**
         * Gets the left value of the field where the difference was found.
         *
         * @return the value
         */
        public Object getLeftValue() {
            return leftValue;
        }


        /**
         * Gets the right value of the field where the difference was found.
         *
         * @return the value
         */
        public Object getRightValue() {
            return rightValue;
        }
    }

    /**
     * Value object that represents a pair of objects that have been compared with eachother. Two instances of this
     * class are equal when the leftObject and rightObject fields reference the same instances.
     */
    protected static class TraversedInstancePair {

        /**
         * The left object
         */
        private Object leftObject;

        /**
         * The right object
         */
        private Object rightObject;

        /**
         * Constructs a new instance with the given left and right object
         *
         * @param leftObject  the left instance
         * @param rightObject the right instance
         */
        public TraversedInstancePair(Object leftObject, Object rightObject) {
            this.leftObject = leftObject;
            this.rightObject = rightObject;
        }

        /**
         * @return The left instance
         */
        public Object getLeftObject() {
            return leftObject;
        }

        /**
         * @return The right instance
         */
        public Object getRightObject() {
            return rightObject;
        }

        /**
         * @param o Another object
         * @return true when the other object is a TraversedInstancePair with the same left and right object instances.
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TraversedInstancePair that = (TraversedInstancePair) o;

            if (!(leftObject == that.leftObject)) return false;
            return rightObject == that.rightObject;
        }

        /**
         * @return This object's hashcode
         */
        @Override
        public int hashCode() {
            int result;
            result = leftObject.hashCode();
            result = 31 * result + rightObject.hashCode();
            return result;
        }
    }
}
