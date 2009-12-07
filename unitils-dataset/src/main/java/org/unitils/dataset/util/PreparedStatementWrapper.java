/*
 * Copyright 2006-2009,  Unitils.org
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
package org.unitils.dataset.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.sql.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public abstract class PreparedStatementWrapper {

    /* The logger instance for this class */
    private static Log logger = LogFactory.getLog(PreparedStatementWrapper.class);

    protected String schemaName;
    protected String tableName;
    protected Connection connection;


    protected Set<String> remainingPrimaryKeyColumnNames;


    protected PreparedStatementWrapper(String schemaName, String tableName, Connection connection) throws SQLException {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.connection = connection;
        this.remainingPrimaryKeyColumnNames = getPrimaryKeyColumnNames(schemaName, tableName, connection);
    }


    public void addColumn(String columnName, String value) {
        boolean primaryKey = isRemainingPrimaryKeyColumn(columnName);
        addColumnName(columnName, primaryKey);
        addValue("?", primaryKey);
        addStatementParameter(value, primaryKey);
    }

    public void addLiteralColumn(String columnName, String value) {
        boolean primaryKey = isRemainingPrimaryKeyColumn(columnName);
        addColumnName(columnName, primaryKey);
        addValue(value, primaryKey);
    }

    public int executeUpdate() throws SQLException {
        String sql = buildStatement();
        List<String> statementValues = getStatementParameters();
        logStatement(sql, statementValues);

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            setStatementValues(preparedStatement, statementValues);
            return preparedStatement.executeUpdate();
        } finally {
            preparedStatement.close();
        }
    }


    protected abstract void addColumnName(String columnName, boolean primaryKey);

    protected abstract void addValue(String value, boolean primaryKey);

    protected abstract String buildStatement();

    protected abstract void addStatementParameter(String value, boolean primaryKey);

    protected abstract List<String> getStatementParameters();


    protected void setStatementValues(PreparedStatement preparedStatement, List<String> statementValues) throws SQLException {
        if (statementValues.isEmpty()) {
            return;
        }
        ParameterMetaData parameterMetaData = preparedStatement.getParameterMetaData();

        int index = 1;
        for (String value : statementValues) {
            int columnTypeInDatabase = parameterMetaData.getParameterType(index);
            preparedStatement.setObject(index++, value, columnTypeInDatabase);
        }
    }

    protected void logStatement(String sql, List<String> statementValues) {
        if (statementValues.isEmpty()) {
            logger.debug("Loading data set values: " + sql);
        } else {
            logger.debug("Loading data set values: " + sql + " -> " + statementValues);
        }
    }


    protected Set<String> getPrimaryKeyColumnNames(String schemaName, String tableName, Connection connection) throws SQLException {
        Set<String> primaryKeyColumnNames = new HashSet<String>();
        ResultSet resultSet = connection.getMetaData().getPrimaryKeys(null, schemaName, tableName);
        while (resultSet.next()) {
            primaryKeyColumnNames.add(resultSet.getString("COLUMN_NAME"));
        }
        return primaryKeyColumnNames;
    }

    protected boolean isRemainingPrimaryKeyColumn(String columnName) {
        Iterator<String> iterator = remainingPrimaryKeyColumnNames.iterator();
        while (iterator.hasNext()) {
            String primaryKeyColumnName = iterator.next();
            if (primaryKeyColumnName.equalsIgnoreCase(columnName)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
}