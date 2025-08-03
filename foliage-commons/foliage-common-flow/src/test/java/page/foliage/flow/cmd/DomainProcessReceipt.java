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
package page.foliage.flow.cmd;

import static page.foliage.common.ioc.InstanceFactory.getInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.flow.FederatedEngine;
import page.foliage.flow.FlowCommandDelegate;
import page.foliage.flow.FlowDelegateExecution;
import page.foliage.ldap.Access;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DomainProcessReceipt extends FlowCommandDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainProcessReceipt.class);

    @Override
    protected void doExecute(FlowDelegateExecution execution, Access access) {
        LOGGER.info("Execute Command: {}", execution.getId());
        String messageId = execution.get(DomainProcessSubmit.VARIABLE_SUBMIT_MESSAGE_ID);
        String executionId = execution.get(DomainProcessSubmit.VARIABLE_SUBMIT_EXECUTION_ID);
        getInstance(FederatedEngine.class).executionEventReceivedMessage(messageId, executionId, execution.map());
    }

}
