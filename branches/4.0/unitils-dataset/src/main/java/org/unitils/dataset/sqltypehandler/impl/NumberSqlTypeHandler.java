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
package org.unitils.dataset.sqltypehandler.impl;

import org.unitils.dataset.sqltypehandler.SqlTypeHandler;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Properties;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class NumberSqlTypeHandler implements SqlTypeHandler<BigDecimal> {

    public void init(Properties configuration) {
    }

    public BigDecimal getValue(String valueAsString, int sqlType) throws Exception {
        return new BigDecimal(valueAsString);
    }

    public BigDecimal getResultSetValue(ResultSet resultSet, int columnIndex, int sqlType) throws Exception {
        return resultSet.getBigDecimal(columnIndex);
    }
}