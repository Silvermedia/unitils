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

package org.unitils.database.core;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.unitils.UnitilsJUnit4;
import org.unitils.core.UnitilsException;
import org.unitils.mock.Mock;
import org.unitils.mock.PartialMock;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tim Ducheyne
 */
public class DataSourceWrapperGetObjectListTest extends UnitilsJUnit4 {

    /* Tested object */
    private PartialMock<DataSourceWrapper> dataSourceWrapper;

    private Mock<SimpleJdbcTemplate> simpleJdbcTemplateMock;


    @Before
    public void initialize() {
        dataSourceWrapper.returns(simpleJdbcTemplateMock).getSimpleJdbcTemplate();
    }


    @Test
    public void getObjectList() throws Exception {
        simpleJdbcTemplateMock.returnsAll(new BigDecimal(1), new BigDecimal(2)).query("query", null, "arg");

        List<BigDecimal> result = dataSourceWrapper.getMock().getObjectList("query", BigDecimal.class, "arg");
        assertEquals(asList(new BigDecimal(1), new BigDecimal(2)), result);
    }

    @Test
    public void exceptionWhenFailure() throws Exception {
        simpleJdbcTemplateMock.raises(new NullPointerException("message")).query("query", null, "arg");
        try {
            dataSourceWrapper.getMock().getObjectList("query", BigDecimal.class, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to execute statement: 'query'.\n" +
                    "Reason: NullPointerException: message", e.getMessage());
        }
    }

    @Test
    public void exceptionWhenNullType() throws Exception {
        try {
            dataSourceWrapper.getMock().getObjectList("query", null, "arg");
            fail("UnitilsException expected");
        } catch (UnitilsException e) {
            assertEquals("Unable to get value list. Type cannot be null.", e.getMessage());
        }
    }
}
