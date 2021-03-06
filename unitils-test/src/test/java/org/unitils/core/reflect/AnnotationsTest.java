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

package org.unitils.core.reflect;

import org.junit.Before;
import org.junit.Test;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.config.Configuration;
import org.unitils.mock.Mock;
import org.unitils.mock.annotation.Dummy;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

/**
 * @author Tim Ducheyne
 */
public class AnnotationsTest extends UnitilsJUnit4 {

    /* Tested object */
    private Annotations<Target> annotations;

    private Mock<Target> annotationMock;

    @Dummy
    private Target classAnnotation1;
    @Dummy
    private Target classAnnotation2;
    @Dummy
    private Configuration configuration;

    private List<Target> classAnnotations;


    @Before
    public void initialize() throws Exception {
        annotationMock.returns(Target.class).annotationType();
        classAnnotations = asList(classAnnotation1, classAnnotation2);
        annotations = new Annotations<Target>(annotationMock.getMock(), classAnnotations, configuration);
    }


    @Test
    public void getAnnotation() {
        Target result = annotations.getAnnotation();
        assertSame(annotationMock.getMock(), result);
    }

    @Test
    public void getClassAnnotations() {
        List<Target> result = annotations.getClassAnnotations();
        assertSame(classAnnotations, result);
    }

    @Test
    public void getType() {
        Class<? extends Annotation> result = annotations.getType();
        assertEquals(Target.class, result);
    }
}
