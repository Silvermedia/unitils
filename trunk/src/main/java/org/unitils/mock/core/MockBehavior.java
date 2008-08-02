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
package org.unitils.mock.core;

/**
 * @author Filip Neven
 * @author Tim Ducheyne
 * @author Kenny Claes
 */
public class MockBehavior {

	private InvocationMatcher invocationMatcher;
	
	private Action action;
	
	
	public MockBehavior(InvocationMatcher invocationMatcher) {
		this(invocationMatcher, null);
	}


	public MockBehavior(InvocationMatcher invocationMatcher, Action action) {
		super();
		this.action = action;
		this.invocationMatcher = invocationMatcher;
	}


	public Object execute(Invocation invocation) throws Throwable {
		return action.execute(invocation);
	}

	
	public boolean matches(Invocation invocation) {
		return invocationMatcher.matches(invocation);
	}


	public InvocationMatcher getInvocationMatcher() {
		return invocationMatcher;
	}

}
