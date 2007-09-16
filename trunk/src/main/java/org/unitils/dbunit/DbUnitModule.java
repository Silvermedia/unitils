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
package org.unitils.dbunit;

import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.core.dbsupport.DbSupport;
import org.unitils.core.dbsupport.DbSupportFactory;
import static org.unitils.core.dbsupport.DbSupportFactory.getDbSupport;
import org.unitils.core.dbsupport.SQLHandler;
import org.unitils.database.DatabaseModule;
import org.unitils.database.transaction.TransactionalDataSource;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.dbunit.datasetfactory.DataSetFactory;
import org.unitils.dbunit.datasetloadstrategy.DataSetLoadStrategy;
import org.unitils.dbunit.util.DbUnitAssert;
import org.unitils.dbunit.util.DbUnitDatabaseConnection;
import org.unitils.dbunit.util.MultiSchemaDataSet;
import static org.unitils.thirdparty.org.apache.commons.io.IOUtils.closeQuietly;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotation;
import static org.unitils.util.AnnotationUtils.getMethodOrClassLevelAnnotationProperty;
import static org.unitils.util.ConfigUtils.getConfiguredInstance;
import static org.unitils.util.ModuleUtils.*;
import static org.unitils.util.ReflectionUtils.createInstanceOfType;
import static org.unitils.util.ReflectionUtils.getClassWithName;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.*;

/**
 * Module that provides support for managing database test data using DBUnit.
 * <p/>
 * Loading of DbUnit data sets can be done by annotating a class or method with the {@link DataSet} annotation. The name
 * of the data set files can be specified explicitly as an argument of the annotation. If no file name is specified, it looks
 * for a file in the same directory as the test class named: 'classname without packagename'.xml.
 * <p/>
 * By annotating a method with the {@link ExpectedDataSet} annotation or by calling the {@link #assertDbContentAsExpected}
 * method, the contents of the database can be compared with the contents of a dataset. The expected dataset can be
 * passed as an argument of the annotation. If no file name is specified it looks for a file in the same directory
 * as the test class that has following name: 'classname without packagename'.'test method name'-result.xml.
 * <p/>
 * This module depends on the {@link DatabaseModule} for database connection management.
 *
 * @author Filip Neven
 * @author Tim Ducheyne
 */
public class DbUnitModule implements Module {

    /**
     * Map holding the default configuration of the dbunit module annotations
     */
    protected Map<Class<? extends Annotation>, Map<String, String>> defaultAnnotationPropertyValues;

    /**
     * Objects that DbUnit uses to connect to the database and to cache some database metadata. Since DBUnit's data
     * caching is time-consuming, this object is created only once and used throughout the entire test run. The
     * underlying JDBC Connection however is 'closed' (returned to the pool) after every database operation.
     * <p/>
     * A different DbUnit connection is used for every database schema. Since DbUnit can only work with a single schema,
     * this is the simplest way to obtain multi-schema support.
     */
    protected Map<String, DbUnitDatabaseConnection> dbUnitDatabaseConnections = new HashMap<String, DbUnitDatabaseConnection>();

    /**
     * The unitils configuration
     */
    protected Properties configuration;


    /**
     * Initializes the DbUnitModule using the given Configuration
     *
     * @param configuration The config, not null
     */
    @SuppressWarnings("unchecked")
    public void init(Properties configuration) {
        this.configuration = configuration;
        defaultAnnotationPropertyValues = getAnnotationPropertyDefaults(DbUnitModule.class, configuration, DataSet.class, ExpectedDataSet.class);
    }


    /**
     * Gets the DbUnit connection or creates one if it does not exist yet.
     *
     * @param schemaName The schema name, not null
     * @return The DbUnit connection, not null
     */
    public DbUnitDatabaseConnection getDbUnitDatabaseConnection(String schemaName) {
        DbUnitDatabaseConnection dbUnitDatabaseConnection = dbUnitDatabaseConnections.get(schemaName);
        if (dbUnitDatabaseConnection == null) {
            dbUnitDatabaseConnection = createDbUnitConnection(schemaName);
            dbUnitDatabaseConnections.put(schemaName, dbUnitDatabaseConnection);
        }
        return dbUnitDatabaseConnection;
    }


    /**
     * This method will first try to load a method level defined dataset. If no such file exists, a class level defined
     * dataset will be loaded. If neither of these files exist, nothing is done.
     * The name of the test data file at both method level and class level can be overridden using the
     * {@link DataSet} annotation. If specified using this annotation but not found, a {@link UnitilsException} is
     * thrown.
     *
     * @param testMethod The method, not null
     * @param testObject The test object, not null
     */
    public void insertDataSet(Method testMethod, Object testObject) {
        try {
            MultiSchemaDataSet multiSchemaDataSet = getDataSet(testMethod, testObject);
            if (multiSchemaDataSet == null) {
                // no dataset specified
                return;
            }
            DataSetLoadStrategy dataSetLoadStrategy = getDataSetOperation(testMethod, testObject.getClass());

            insertDataSet(multiSchemaDataSet, dataSetLoadStrategy);
        } catch (Exception e) {
            throw new UnitilsException("Error inserting test data from DbUnit dataset for method " + testMethod, e);
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Inserts the default dataset for the given test class into the database
     *
     * @param testClass The test class for which the default dataset must be loaded
     */
    public void insertDefaultDataSet(Class<?> testClass) {
        DataSetFactory dataSetFactory = getDefaultDataSetFactory();
        String[] dataSetFileNames = new String[]{getDefaultDataSetFileName(testClass, dataSetFactory.getDataSetFileExtension())};
        insertDataSet(testClass, dataSetFileNames);
    }


    /**
     * Inserts the dataset consisting of the given list of files into the database
     *
     * @param testClass        The test class for which the dataset must be loaded
     * @param dataSetFileNames The names of the files that define the test data
     */
    public void insertDataSet(Class<?> testClass, String... dataSetFileNames) {
        DataSetFactory dataSetFactory = getDefaultDataSetFactory();
        DataSetLoadStrategy dataSetLoadStrategy = getDefaultDataSetLoadStrategy();
        MultiSchemaDataSet dataSet = getDataSet(testClass, dataSetFileNames, dataSetFactory);
        insertDataSet(dataSet, dataSetLoadStrategy);
    }


    /**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>,
     * using the default {@link DataSetLoadStrategy} and {@link DataSetFactory} class.
     *
     * @param inputStream The stream containing the test data set, not null
     */
    @SuppressWarnings("unchecked")
    public void insertDataSet(InputStream inputStream) {
        DataSetFactory dataSetFactory = getDefaultDataSetFactory();
        DataSetLoadStrategy dataSetLoadStrategy = getDefaultDataSetLoadStrategy();
        insertDataSet(inputStream, dataSetFactory, dataSetLoadStrategy);
    }


    /**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>
     *
     * @param inputStream              The stream containing the test data set, not null
     * @param dataSetFactoryClass      The class of the factory that must be used to read this dataset
     * @param dataSetLoadStrategyClass The class of the load strategy that must be used to load this dataset
     */
    public void insertDataSet(InputStream inputStream, Class<? extends DataSetFactory> dataSetFactoryClass, Class<? extends DataSetLoadStrategy> dataSetLoadStrategyClass) {
        DataSetLoadStrategy dataSetLoadStrategy = createInstanceOfType(dataSetLoadStrategyClass, false);
        DataSetFactory dataSetFactory = createInstanceOfType(dataSetFactoryClass, false);
        insertDataSet(inputStream, dataSetFactory, dataSetLoadStrategy);
    }


    /**
     * Inserts the test data coming from the DbUnit dataset file coming from the given <code>InputStream</code>
     *
     * @param inputStream         The stream containing the test data set, not null
     * @param dataSetFactory      The factory that must be used to read this dataset inputstream
     * @param dataSetLoadStrategy The load strategy that must be used to load this dataset
     */
    protected void insertDataSet(InputStream inputStream, DataSetFactory dataSetFactory, DataSetLoadStrategy dataSetLoadStrategy) {
        dataSetFactory.init(getDefaultDbSupport().getSchemaName());
        MultiSchemaDataSet multiSchemaDataSet = dataSetFactory.createDataSet(inputStream);
        insertDataSet(multiSchemaDataSet, dataSetLoadStrategy);
    }


    /**
     * Loads the given multi schema dataset into the database, using the given loadstrategy
     *
     * @param multiSchemaDataSet  The multi schema dataset that is inserted in the database
     * @param dataSetLoadStrategy The load strategy that is used
     */
    protected void insertDataSet(MultiSchemaDataSet multiSchemaDataSet, DataSetLoadStrategy dataSetLoadStrategy) {
        try {
            for (String schemaName : multiSchemaDataSet.getSchemaNames()) {
                IDataSet schemaDataSet = multiSchemaDataSet.getDataSetForSchema(schemaName);
                dataSetLoadStrategy.execute(getDbUnitDatabaseConnection(schemaName), schemaDataSet);
            }
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Compares the contents of the expected DbUnitDataSet with the contents of the database. Only the tables and columns
     * that occur in the expected DbUnitDataSet are compared with the database contents.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     */
    public void assertDbContentAsExpected(Method testMethod, Object testObject) {
        try {
            // get the expected dataset
            MultiSchemaDataSet multiSchemaExpectedDataSet = getExpectedDataSet(testMethod, testObject);
            if (multiSchemaExpectedDataSet == null) {
                // no data set should be compared
                return;
            }
            // first make sure every database update is flushed to the database
            getDatabaseModule().flushDatabaseUpdates(testObject);

            for (String schemaName : multiSchemaExpectedDataSet.getSchemaNames()) {
                IDataSet compositeDataSet = multiSchemaExpectedDataSet.getDataSetForSchema(schemaName);
                DbUnitAssert.assertDbContentAsExpected(compositeDataSet, getDbUnitDatabaseConnection(schemaName));
            }
        } finally {
            closeJdbcConnection();
        }
    }


    /**
     * Using the values of the method-level or class-level {@link DataSet} annotations, returns the data set for the
     * given test method. If no method-level or class-level {@link DataSet} annotation is found, null is returned.
     * If a method-level {@link DataSet} annotation is found this will be used, else the class-level will be used.
     * <p/>
     * The value of the found annotation determines which files need to be used for the dataset. If one or more filenames are
     * explicitly specified, these names will be used. Filenames that start with '/' are treated absolute. Filenames
     * that do not start with '/', are relative to the current class.
     * If an empty filename ("") is specified, this method will look for a file named 'classname'.xml.
     * <p/>
     * If a file is not found or could not be loaded (but was requested, because there is an annotation), an exception
     * is raised.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     * @return The dataset, null if no {@link DataSet} annotation is found.
     */
    public MultiSchemaDataSet getDataSet(Method testMethod, Object testObject) {
        Class<?> testClass = testObject.getClass();
        DataSet dataSetAnnotation = getMethodOrClassLevelAnnotation(DataSet.class, testMethod, testClass);
        if (dataSetAnnotation == null) {
            // No @DataSet annotation found
            return null;
        }

        // Create configured factory for data sets
        DataSetFactory dataSetFactory = getDataSetFactory(DataSet.class, testMethod, testClass);

        // Get the dataset file name
        String[] dataSetFileNames = dataSetAnnotation.value();
        if (dataSetFileNames.length == 0) {
            // empty means, use default file name, which is the name of the class + extension
            dataSetFileNames = new String[]{getDefaultDataSetFileName(testClass, dataSetFactory.getDataSetFileExtension())};
        }

        return getDataSet(testClass, dataSetFileNames, dataSetFactory);
    }


    /**
     * Returns the {@link MultiSchemaDataSet} that represents the state of a number of database tables after the given
     * <code>Method</code> has been executed.
     *
     * @param testMethod The test method, not null
     * @param testObject The test object, not null
     * @return The dataset, null if there is no data set
     */
    public MultiSchemaDataSet getExpectedDataSet(Method testMethod, Object testObject) {
        Class<? extends Object> testClass = testObject.getClass();
        ExpectedDataSet expectedDataSetAnnotation = getMethodOrClassLevelAnnotation(ExpectedDataSet.class, testMethod, testClass);
        if (expectedDataSetAnnotation == null) {
            // No @ExpectedDataSet annotation found
            return null;
        }

        // Create configured factory for data sets
        DataSetFactory dataSetFactory = getDataSetFactory(ExpectedDataSet.class, testMethod, testClass);

        // Get the dataset file name
        String[] dataSetFileNames = expectedDataSetAnnotation.value();
        if (dataSetFileNames.length == 0) {
            // empty means use default file name
            dataSetFileNames = new String[]{getDefaultExpectedDataSetFileName(testMethod, testClass, dataSetFactory.getDataSetFileExtension())};
        }

        return getDataSet(testMethod.getDeclaringClass(), dataSetFileNames, dataSetFactory);
    }


    /**
     * Creates the dataset for the given file. Filenames that start with '/' are treated absolute. Filenames that
     * do not start with '/', are relative to the current class.
     *
     * @param testClass        The test class, not null
     * @param dataSetFileNames The names of the files, (start with '/' for absolute names), not null, not empty
     * @param dataSetFactory   DataSetFactory responsible for creating the dataset file
     * @return The data set, null if the file does not exist
     */
    protected MultiSchemaDataSet getDataSet(Class<?> testClass, String[] dataSetFileNames, DataSetFactory dataSetFactory) {
        List<InputStream> dataSetInputStreams = new ArrayList<InputStream>();
        try {
            for (String dataSetFileName : dataSetFileNames) {
                InputStream dataSetInputStream = testClass.getResourceAsStream(dataSetFileName);
                if (dataSetInputStream == null) {
                    throw new UnitilsException("DataSet file with name " + dataSetFileName + " cannot be found");
                }
                dataSetInputStreams.add(dataSetInputStream);
            }
            return dataSetFactory.createDataSet(dataSetInputStreams.toArray(new InputStream[0]));

        } finally {
            for (InputStream inputStream : dataSetInputStreams) {
                closeQuietly(inputStream);
            }
        }
    }


    /**
     * Creates the DbUnit dataset operation for loading a data set for the given method. If a value for loadStrategy is
     * found on an annotation, this class is used, otherwise the configured default class will be used.
     *
     * @param testMethod The method, not null
     * @param testClass  The test class, not null
     * @return The DbUnit operation, not null
     */
    @SuppressWarnings({"unchecked"})
    protected DataSetLoadStrategy getDataSetOperation(Method testMethod, Class testClass) {
        Class<? extends DataSetLoadStrategy> dataSetOperationClass = getMethodOrClassLevelAnnotationProperty(DataSet.class, "loadStrategy", DataSetLoadStrategy.class, testMethod, testClass);
        dataSetOperationClass = (Class<? extends DataSetLoadStrategy>) getClassValueReplaceDefault(DataSet.class, "loadStrategy", dataSetOperationClass, defaultAnnotationPropertyValues, DataSetLoadStrategy.class);
        return createInstanceOfType(dataSetOperationClass, false);
    }


    /**
     * Creates a new instance of dbUnit's <code>IDatabaseConnection</code>
     *
     * @param schemaName The schema name, not null
     * @return A new instance of dbUnit's <code>IDatabaseConnection</code>
     */
    protected DbUnitDatabaseConnection createDbUnitConnection(String schemaName) {
        // A db support instance is created to get the schema name in correct casing
        TransactionalDataSource dataSource = getDatabaseModule().getDataSource();
        SQLHandler sqlHandler = new SQLHandler(dataSource);
        DbSupport dbSupport = getDbSupport(configuration, sqlHandler, schemaName);

        // Create connection
        DbUnitDatabaseConnection connection = new DbUnitDatabaseConnection(dataSource, dbSupport.getSchemaName());

        /* Create DbUnits IDataTypeFactory, that handles dbms specific data type issues */
        IDataTypeFactory dataTypeFactory = (IDataTypeFactory) getConfiguredInstance(IDataTypeFactory.class, configuration, dbSupport.getDatabaseDialect());

        // Make sure correct dbms specific data types are used
        DatabaseConfig config = connection.getConfig();
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        return connection;
    }


    /**
     * Closes (i.e. return to the pool) the JDBC Connection that is currently in use by the DbUnitDatabaseConnection
     */
    protected void closeJdbcConnection() {
        try {
            for (DbUnitDatabaseConnection dbUnitDatabaseConnection : dbUnitDatabaseConnections.values()) {
                dbUnitDatabaseConnection.closeJdbcConnection();
            }
        } catch (SQLException e) {
            throw new UnitilsException("Error while closing connection.", e);
        }
    }


    /**
     * Gets the name of the default testdata file at class level The default name is constructed as
     * follows: 'classname without packagename'.xml
     *
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files
     * @return The default filename, not null
     */
    protected String getDefaultDataSetFileName(Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + '.' + extension;
    }


    /**
     * Gets the name of the expected dataset file. The default name of this file is constructed as
     * follows: 'classname without packagename'.'testname'-result.xml.
     *
     * @param method    The test method, not null
     * @param testClass The test class, not null
     * @param extension The configured extension of dataset files, not null
     * @return The expected dataset filename, not null
     */
    protected static String getDefaultExpectedDataSetFileName(Method method, Class<?> testClass, String extension) {
        String className = testClass.getName();
        return className.substring(className.lastIndexOf(".") + 1) + "." + method.getName() + "-result." + extension;
    }


    /**
     * @return The default {@link DataSetFactory} class as configured in unitils
     */
    @SuppressWarnings("unchecked")
    protected DataSetFactory getDefaultDataSetFactory() {
        return getDataSetFactory((Class<? extends DataSetFactory>) getClassWithName(getAnnotationPropertyDefault(DbUnitModule.class, DataSet.class, "factory", configuration)));
    }


    /**
     * Get the configured DataSetFactory for the given method
     *
     * @param annotationClass The class of the annotation, i.e. DataSet.class or ExpectedDataSet.class
     * @param testMethod      The method for which we need the configured DataSetFactory
     * @param testClass       The class that is looked for class-level annotations
     * @return The configured DataSetFactory
     */
    @SuppressWarnings("unchecked")
    protected DataSetFactory getDataSetFactory(Class<? extends Annotation> annotationClass, Method testMethod, Class testClass) {
        Class<? extends DataSetFactory> dataSetFactoryClass = getMethodOrClassLevelAnnotationProperty(annotationClass, "factory", DataSetFactory.class, testMethod, testClass);
        dataSetFactoryClass = (Class<? extends DataSetFactory>) getClassValueReplaceDefault(annotationClass, "factory", dataSetFactoryClass, defaultAnnotationPropertyValues, DataSetFactory.class);
        return getDataSetFactory(dataSetFactoryClass);
    }


    /**
     * Creates and initializes a data set factory of the given type.
     *
     * @param dataSetFactoryClass The type, not null
     * @return The {@link DataSetFactory} with the given class
     */
    protected DataSetFactory getDataSetFactory(Class<? extends DataSetFactory> dataSetFactoryClass) {
        DataSetFactory dataSetFactory = createInstanceOfType(dataSetFactoryClass, false);
        dataSetFactory.init(getDefaultDbSupport().getSchemaName());
        return dataSetFactory;
    }


    /**
     * @return The default {@link DataSetLoadStrategy} class as configured in unitils
     */
    @SuppressWarnings("unchecked")
    protected DataSetLoadStrategy getDefaultDataSetLoadStrategy() {
        return createInstanceOfType((Class<? extends DataSetLoadStrategy>) getClassWithName(getAnnotationPropertyDefault(DbUnitModule.class, DataSet.class, "loadStrategy", configuration)), false);
    }


    /**
     * @return The default DbSupport (the one that connects to the default database schema)
     */
    protected DbSupport getDefaultDbSupport() {
        DataSource dataSource = getDatabaseModule().getDataSource();
        SQLHandler sqlHandler = new SQLHandler(dataSource);
        return DbSupportFactory.getDefaultDbSupport(configuration, sqlHandler);
    }


    /**
     * @return Implementation of DatabaseModule, on which this module is dependent
     */
    protected DatabaseModule getDatabaseModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(DatabaseModule.class);
    }


    /**
     * @return The TestListener object that implements Unitils' DbUnit support
     */
    public TestListener createTestListener() {
        return new DbUnitListener();
    }


    /**
     * Test listener that is called while the test framework is running tests
     */
    protected class DbUnitListener extends TestListener {

        @Override
        public void beforeTestMethod(Object testObject, Method testMethod) {
            insertDataSet(testMethod, testObject);
        }

        @Override
        public void afterTestMethod(Object testObject, Method testMethod, Throwable throwable) {
            if (throwable == null) {
                assertDbContentAsExpected(testMethod, testObject);
            }
        }

    }

}
