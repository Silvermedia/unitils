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
package org.unitils.io.reader;

import java.net.URI;

/**
 * @author Tim Ducheyne
 * @author Jeroen Horemans
 * @since 3.3
 */
public interface FileResolvingStrategy {

    /**
     * Resolves the location for a file with the default name: 'classname'.'extension'.
     * An exception is raised if the file could not be found.
     *
     * @param extension The extension of the file, not null
     * @param testClass The test class, not null
     * @return The file, not null
     */
    URI resolveDefaultFileName(String extension, Class<?> testClass);

    /**
     * Resolves the location for a file with a certain name.
     * An exception is raised if the file could not be found.
     *
     * @param fileName  The name of the file, not null
     * @param testClass The test class, not null
     * @return The file, not null
     */
    URI resolveFileName(String fileName, Class<?> testClass);
}
