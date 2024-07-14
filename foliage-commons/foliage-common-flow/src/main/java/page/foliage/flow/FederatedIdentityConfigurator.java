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

import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.idm.engine.IdmEngineConfiguration;
import org.flowable.idm.engine.configurator.IdmEngineConfigurator;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FederatedIdentityConfigurator extends IdmEngineConfigurator {

    // ------------------------------------------------------------------------

    @Override
    public void beforeInit(AbstractEngineConfiguration engineConfiguration) {
        // DO NOTHING
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        super.configure(engineConfiguration);
        FederatedIdentityService service = new FederatedIdentityService(idmEngineConfiguration);
        getIdmEngineConfiguration(engineConfiguration).setIdmIdentityService(service);
    }

    // ------------------------------------------------------------------------

    private static IdmEngineConfiguration getIdmEngineConfiguration(AbstractEngineConfiguration engineConfiguration) {
        return (IdmEngineConfiguration) engineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_IDM_ENGINE_CONFIG);
    }

}
