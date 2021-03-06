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

import org.unitils.mock.argumentmatcher.Capture;
import org.unitils.mock.core.proxy.Argument;

/**
 * @author Tim Ducheyne
 */
public class CaptureArgumentMatcher<T> extends AnyArgumentMatcher<T> {

    private Capture<T> capture;


    /**
     * @param capture The object that will hold the captured argument value, not null
     */
    public CaptureArgumentMatcher(Capture<T> capture) {
        super(capture.getType());
        this.capture = capture;
    }


    @Override
    public void matched(Argument<T> argument) {
        capture.setArgument(argument);
    }
}