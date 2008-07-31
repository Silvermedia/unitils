/*
 * Copyright 2006-2007,  Unitils.org
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
package org.unitils.mock.core.argumentmatcher;

import org.unitils.mock.core.ArgumentMatcher;

/**
 * An {@link ArgumentMatcher} implementation that will match if the given argument is (object) identical to the <code>Object</code> passed on construction.
 * 
 * @author Kenny Claes
 * @author Filip Neven
 * @author Tim Ducheyne
 *
 */
public class SameArgumentMatcher implements ArgumentMatcher{

	private final Object o;

	/**
	 * Constructor.
	 * 
	 * @param o the object to check for object identity. Not null.
	 */
	public SameArgumentMatcher(Object o) {
		this.o = o;
	}
	
	/*
	 * @see ArgumentMatcher#matches(Object)
	 */
	public boolean matches(Object o) {
		return this.o == o;
	}

}