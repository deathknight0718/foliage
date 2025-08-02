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

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import page.foliage.common.util.CodecUtils;
import page.foliage.guava.common.base.Preconditions;
import page.foliage.ldap.Access;

/**
 * 
 * @author deathknight0718@qq.com
 */
public abstract class FlowCommandDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        Preconditions.checkState(execution.hasVariable(FlowVariables.KEY_ACCESS_ID), "Access is required");
        Access.register(Access.get(CodecUtils.decodeHex36(execution.getVariable(FlowVariables.KEY_ACCESS_ID).toString())));
        doExecute(new FlowDelegateExecution(execution));
        Access.unregister();
    }

    abstract protected void doExecute(FlowDelegateExecution execution);

}
