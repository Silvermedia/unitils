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
package org.unitils.mock.core.proxy;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Tim Ducheyne
 */
public class ProxyInvocationIsEqualsMethodTest {

    private ProxyInvocation proxyInvocation;


    @Test
    public void trueWhenEqualsMethod() throws Exception {
        Method method = MyClass.class.getMethod("equals", Object.class);
        proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        boolean result = proxyInvocation.isEqualsMethod();
        assertTrue(result);
    }

    @Test
    public void falseWhenNotEqualsMethod() throws Exception {
        Method method = MyClass.class.getMethod("method");
        proxyInvocation = new ProxyInvocation(null, null, null, method, null, null);

        boolean result = proxyInvocation.isEqualsMethod();
        assertFalse(result);
    }


    private static class MyClass {
        public void method() {
        }
    }
}
