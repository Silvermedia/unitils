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

import org.apache.commons.lang.ArrayUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Class containing collection related utilities
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class CollectionUtils {

    /**
     * Converts the given array of elements to a set.
     *
     * @param elements The elements
     * @return The elements as a set, empty if elements was null
     */
    public static <T> Set<T> asSet(T... elements) {
        Set<T> result = new LinkedHashSet<T>();
        if (elements == null) {
            return result;
        }
        result.addAll(asList(elements));
        return result;
    }

    /**
     * Converts the given array or collection object (possibly primitive array) to type Collection
     *
     * @param object The array or collection
     * @return The object collection
     */
    public static Collection<?> convertToCollection(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Collection<?>) {
            return (Collection<?>) object;
        }
        // If needed convert primitive array to object array
        Object[] objectArray = convertToObjectArray(object);
        // Convert array to collection
        return asList(objectArray);
    }

    /**
     * Converts the given array object (possibly primitive array) to type Object[]
     *
     * @param object The array
     * @return The object array
     */
    public static Object[] convertToObjectArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof byte[]) {
            return ArrayUtils.toObject((byte[]) object);
        }
        if (object instanceof short[]) {
            return ArrayUtils.toObject((short[]) object);
        }
        if (object instanceof int[]) {
            return ArrayUtils.toObject((int[]) object);
        }
        if (object instanceof long[]) {
            return ArrayUtils.toObject((long[]) object);
        }
        if (object instanceof char[]) {
            return ArrayUtils.toObject((char[]) object);
        }
        if (object instanceof float[]) {
            return ArrayUtils.toObject((float[]) object);
        }
        if (object instanceof double[]) {
            return ArrayUtils.toObject((double[]) object);
        }
        if (object instanceof boolean[]) {
            return ArrayUtils.toObject((boolean[]) object);
        }
        return (Object[]) object;

    }
}
