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
package org.unitils.mock.util;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.unitils.core.UnitilsException;

/**
 * Utility class to create proxy objects.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne 
 */
public class ProxyUtil {

	/**
	 * Creates a proxy object for the given <code>Class</code>.
	 * A <code>MethodInterceptor</code> must be passed to hook into the proxy.
	 * @param interceptor The interceptor to hook into the proxy. Not null.
	 * @param targetClass The class of the proxy to be created. Not null.
	 * 
	 * @param <T> The type you want to create a proxy for.
	 * @return the proxy object.
	 */
	public static <T> T createProxy(MethodInterceptor interceptor, Class<T> targetClass, Class<?>... interfaces) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setInterfaces(interfaces);
        enhancer.setCallbackType(MethodInterceptor.class);
        enhancer.setUseFactory(true);
        @SuppressWarnings("unchecked")
        final Class<T> enhancedTargetClass = enhancer.createClass();
        
        final Objenesis objenesis = new ObjenesisStd();
        @SuppressWarnings("unchecked")
        final Factory proxy = (Factory) objenesis.newInstance(enhancedTargetClass);
        proxy.setCallbacks(new Callback[] { interceptor });

        return (T) proxy;
	}
	
	
	public static Method getOriginalMethod(Method proxyMethod, Class<?> proxiedClass) {
		try {
			return proxiedClass.getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
		} catch (NoSuchMethodException e) {
			throw new UnitilsException(e);
		}
	}
	
	
	public static StackTraceElement getProxiedMethodInvokedAt(StackTraceElement[] invocationStackTrace) {
		for (int stackTraceIndex = 0; stackTraceIndex < invocationStackTrace.length - 1; stackTraceIndex++) {
			if (invocationStackTrace[stackTraceIndex].getClassName().contains("$$EnhancerByCGLIB$$")) {
				return invocationStackTrace[stackTraceIndex + 1];
			}
		}
		throw new UnitilsException("No invocation from a cglib proxy found in stacktrace");
	}
}
