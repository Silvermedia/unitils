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
package org.unitils.dataset.annotation.handler.impl;

import org.unitils.dataset.DataSetModule;
import org.unitils.dataset.annotation.InlineRefreshDataSet;
import org.unitils.dataset.annotation.handler.DataSetAnnotationHandler;
import org.unitils.dataset.factory.DataSetStrategyHandlerFactory;
import org.unitils.dataset.loadstrategy.InlineLoadDataSetStrategyHandler;

import java.lang.reflect.Method;

/**
 * Handles the execution of the {@link org.unitils.dataset.annotation.InlineRefreshDataSet} annotation.
 *
 * @author Tim Ducheyne
 * @author Filip Neven
 */
public class InlineRefreshDataSetAnnotationHandler implements DataSetAnnotationHandler<InlineRefreshDataSet> {


    public void handle(InlineRefreshDataSet annotation, Method testMethod, Object testInstance, DataSetModule dataSetModule) {
        String[] dataSetRows = annotation.value();
        String databaseName = annotation.databaseName();

        DataSetStrategyHandlerFactory dataSetStrategyHandlerFactory = dataSetModule.getDataSetStrategyHandlerFactory();
        InlineLoadDataSetStrategyHandler inlineLoadDataSetStrategyHandler = dataSetStrategyHandlerFactory.createInlineLoadDataSetStrategyHandler(databaseName);
        inlineLoadDataSetStrategyHandler.refreshDataSet(dataSetRows);
    }

}