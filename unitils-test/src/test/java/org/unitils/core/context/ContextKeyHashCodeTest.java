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

package org.unitils.core.context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.unitils.core.context.Context.Key;

/**
 * @author Tim Ducheyne
 */
public class ContextKeyHashCodeTest {


    @Test
    public void hashCodeForTypeAndClassifiers() {
        Key key = new Key(StringBuffer.class, "a", "b");
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void sameHashCodeWhenEqual() {
        Key key1 = new Key(StringBuffer.class, "a", "b");
        Key key2 = new Key(StringBuffer.class, "a", "b");

        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    public void nullType() {
        Key key = new Key(null, "a", "b");
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullClassifiers() {
        Key key = new Key(StringBuffer.class);
        int result = key.hashCode();

        assertTrue(result != 0);
    }

    @Test
    public void nullTypeAndClassifiers() {
        Key key = new Key(null, null);
        int result = key.hashCode();

        assertTrue(result != 0);
    }
}
