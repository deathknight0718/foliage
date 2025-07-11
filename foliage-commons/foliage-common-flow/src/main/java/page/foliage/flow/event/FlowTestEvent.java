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
package page.foliage.flow.event;

import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.common.ioc.InstanceFactory;
import page.foliage.flow.FederatedEngine;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowTestEvent extends AbstractFlowableEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlowTestEvent.class);

    public void submit() {
        InstanceFactory.getInstance(FederatedEngine.class).submitEventListener(this);
    }

    public void remove() {
        InstanceFactory.getInstance(FederatedEngine.class).removeEventListener(this);
    }

    @Override
    public void onEvent(FlowableEvent event) {
        LOGGER.info("flow event handled: {}", event.getType().name());
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }

}
