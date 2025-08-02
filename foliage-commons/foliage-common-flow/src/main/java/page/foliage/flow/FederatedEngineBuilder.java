/*
 * Copyright 2023 Foliage Develop Team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package page.foliage.flow;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedEngineBuilder {

    // ------------------------------------------------------------------------

    public static final String DB_SCHEMA = "flow";

    // ------------------------------------------------------------------------

    private ProcessEngineConfiguration configuration = new StandaloneProcessEngineConfiguration();

    // ------------------------------------------------------------------------

    public FederatedEngineBuilder withDataSource(DataSource source) {
        configuration.setDataSource(source);
        return this;
    }

    public FederatedEngineBuilder withAsyncExecutorActivate(boolean asyncExecutorActivate) {
        configuration.setAsyncExecutorActivate(asyncExecutorActivate);
        return this;
    }

    public FederatedEngineBuilder withEnableEventDispatcher(boolean enableEventDispatcher) {
        configuration.setEnableEventDispatcher(enableEventDispatcher);
        return this;
    }

    // ------------------------------------------------------------------------

    public FederatedEngine build() {
        configuration.setDatabaseType(ProcessEngineConfiguration.DATABASE_TYPE_POSTGRES);
        configuration.setDatabaseSchema(DB_SCHEMA);
        configuration.setDatabaseSchemaUpdate(AbstractEngineConfiguration.DB_SCHEMA_UPDATE_FALSE);
        configuration.setIdmEngineConfigurator(new FederatedIdentityConfigurator());
        ProcessEngine engine = configuration.buildProcessEngine();
        return new FederatedEngine(engine);
    }

}
