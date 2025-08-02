/*
 * Copyright 2025 Foliage Develop Team.
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

import static page.foliage.common.ioc.InstanceFactory.getInstance;

import org.flowable.engine.runtime.Execution;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class FlowExecution {

    private final Execution delegate;

    public FlowExecution(Execution delegate) {
        this.delegate = delegate;
    }

    public String getId() {
        return delegate.getId();
    }

    public void receivedMessage(String name, FlowVariables variables) {
        getInstance(FederatedEngine.class).executionEventReceivedMessage(name, delegate.getId(), variables);
    }

    public void receivedSignal(String name, FlowVariables variables) {
        getInstance(FederatedEngine.class).executionEventReceivedSignal(name, delegate.getId(), variables);
    }

    public void trigger() {
        getInstance(FederatedEngine.class).executionTrigger(delegate.getId());
    }

}
