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

package org.unitils.database.transaction;

import org.unitils.core.spring.SpringTestManager;
import org.unitils.database.transaction.impl.DefaultTransactionProvider;
import org.unitils.database.transaction.impl.SpringApplicationContextTransactionProvider;

/**
 * @author Tim Ducheyne
 */
public class TransactionProviderManager {

    protected SpringTestManager springTestManager;
    protected DefaultTransactionProvider defaultTransactionProvider;
    protected SpringApplicationContextTransactionProvider springApplicationContextTransactionProvider;


    public TransactionProviderManager(SpringTestManager springTestManager, DefaultTransactionProvider defaultTransactionProvider, SpringApplicationContextTransactionProvider springApplicationContextTransactionProvider) {
        this.springTestManager = springTestManager;
        this.defaultTransactionProvider = defaultTransactionProvider;
        this.springApplicationContextTransactionProvider = springApplicationContextTransactionProvider;
    }


    public TransactionProvider getTransactionProvider() {
        if (springTestManager.isTestWithApplicationContext()) {
            return springApplicationContextTransactionProvider;
        }
        return defaultTransactionProvider;
    }
}
