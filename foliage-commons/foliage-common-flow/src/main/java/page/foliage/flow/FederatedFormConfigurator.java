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

import java.util.List;

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.AbstractEngineConfigurator;
import org.flowable.common.engine.impl.EngineDeployer;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.common.engine.impl.persistence.entity.Entity;
import org.flowable.form.engine.FormEngineConfiguration;
import org.flowable.form.engine.impl.cfg.StandaloneFormEngineConfiguration;
import org.flowable.idm.engine.impl.db.EntityDependencyOrder;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedFormConfigurator extends AbstractEngineConfigurator {

    // ------------------------------------------------------------------------

    protected FormEngineConfiguration formEngineConfiguration;

    // ------------------------------------------------------------------------

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        // DO NOTHING
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (formEngineConfiguration == null) {
            formEngineConfiguration = new StandaloneFormEngineConfiguration();
        }
        initialiseCommonProperties(engineConfiguration, formEngineConfiguration);
        formEngineConfiguration.buildFormEngine();
        initServiceConfigurations(engineConfiguration, formEngineConfiguration);
        // TODO: build form services.
        getFormEngineConfiguration(engineConfiguration).setFormService(null);
        getFormEngineConfiguration(engineConfiguration).setFormManagementService(null);
        getFormEngineConfiguration(engineConfiguration).setFormRepositoryService(null);
    }

    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_FORM;
    }

    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        return null;
    }

    @Override
    protected String getMybatisCfgPath() {
        return FormEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }

    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }

    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    // ------------------------------------------------------------------------

    private static FormEngineConfiguration getFormEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (FormEngineConfiguration) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_FORM_ENGINE_CONFIG);
    }

}
