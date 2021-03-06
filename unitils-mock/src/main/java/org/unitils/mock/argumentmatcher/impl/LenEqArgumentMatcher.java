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
package org.unitils.mock.argumentmatcher.impl;

import org.unitils.mock.argumentmatcher.ArgumentMatcher;
import org.unitils.mock.core.proxy.Argument;
import org.unitils.reflectionassert.ReflectionComparator;

import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.MATCH;
import static org.unitils.mock.argumentmatcher.ArgumentMatcher.MatchResult.NO_MATCH;
import static org.unitils.reflectionassert.ReflectionComparatorFactory.createRefectionComparator;
import static org.unitils.reflectionassert.ReflectionComparatorMode.IGNORE_DEFAULTS;
import static org.unitils.reflectionassert.ReflectionComparatorMode.LENIENT_ORDER;

/**
 * A matcher for checking whether an argument equals a given value.
 * Reflection is used to compare all fields of these values. The actual order of collections will be ignored and only
 * fields that have a non default value will be compared.
 *
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class LenEqArgumentMatcher<T> extends ArgumentMatcher<T> {

    /* The expected value */
    protected T value;


    /**
     * Creates a matcher for the given value. A copy of the value is taken so that it can be compared
     * even when the value itself was modified later-on.
     *
     * @param value The expected value
     */
    public LenEqArgumentMatcher(T value) {
        this.value = value;
    }


    /**
     * Returns true if the given object matches the expected argument, false otherwise.
     * <p/>
     * The argumentAtInvocationTime is a copy (deep clone) of the arguments at the time of
     * the invocation. This way the original values can still be used later-on even when changes
     * occur to the original values (pass-by-value vs pass-by-reference).
     *
     * @param argument The argument to match, not null
     * @return The match result, not null
     */
    @Override
    public MatchResult matches(Argument<T> argument) {
        ReflectionComparator reflectionComparator;
        if (value instanceof Character || value instanceof Number || value instanceof Boolean) {
            reflectionComparator = createRefectionComparator();
        } else {
            reflectionComparator = createRefectionComparator(LENIENT_ORDER, IGNORE_DEFAULTS);
        }
        T argumentValueAtInvocationTime = argument.getValueAtInvocationTime();
        if (reflectionComparator.isEqual(value, argumentValueAtInvocationTime)) {
            return MATCH;
        }
        return NO_MATCH;
    }
}
