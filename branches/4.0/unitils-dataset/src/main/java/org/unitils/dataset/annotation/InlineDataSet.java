/*
 * Copyright Unitils.org
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
package org.unitils.dataset.annotation;

import org.unitils.dataset.annotation.handler.MarkerForLoadDataSetAnnotation;
import org.unitils.dataset.annotation.handler.impl.InlineDataSetAnnotationHandler;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * todo javadoc
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Inherited
@MarkerForLoadDataSetAnnotation(InlineDataSetAnnotationHandler.class)
public @interface InlineDataSet {

    String[] value() default {};

    /**
     * @return The name of the database on which the data set(s) need to be asserted, defaults to the default database
     */
    String databaseName() default "";

}