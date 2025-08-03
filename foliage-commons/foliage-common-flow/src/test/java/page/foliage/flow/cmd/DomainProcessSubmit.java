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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import page.foliage.common.collect.Identities;
import page.foliage.flow.FlowCommandDelegate;
import page.foliage.flow.FlowDefinition;
import page.foliage.flow.FlowDelegateExecution;
import page.foliage.flow.FlowProcess;
import page.foliage.ldap.Access;
import page.foliage.ldap.Domain;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class DomainProcessSubmit extends FlowCommandDelegate {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainProcessSubmit.class);

    // ------------------------------------------------------------------------

    public static final String VARIABLE_SUBMIT_DEFINITION_KEY = "_submitDefinitionKey";

    public static final String VARIABLE_SUBMIT_DOMAIN_ID = "_submitDomainId";

    public static final String VARIABLE_SUBMIT_DOMAIN_IDENTIFIER = "_submitDomainIdentifier";

    public static final String VARIABLE_SUBMIT_EXECUTION_ID = "_submitExecutionId";

    public static final String VARIABLE_SUBMIT_MESSAGE_ID = "_submitMessageId";

    // ------------------------------------------------------------------------

    @Override
    protected void doExecute(FlowDelegateExecution execution, Access access) {
        LOGGER.info("Execute Command: {}", execution.getId());
        String definitionKey = execution.getVariable(VARIABLE_SUBMIT_DEFINITION_KEY).asText();
        Long domainId = execution.getVariable(VARIABLE_SUBMIT_DOMAIN_ID).asLong();
        Domain domain = Domain.get(domainId);
        FlowDefinition definition = FlowDefinition.latest(Access.current(), definitionKey, domain);
        execution.setVariable(VARIABLE_SUBMIT_MESSAGE_ID, Identities.uuid().toString());
        execution.setVariable(VARIABLE_SUBMIT_EXECUTION_ID, execution.getId());
        FlowProcess.Starter starter = definition.starter(Access.current());
        starter.variables(execution.getVariables()).start();
    }

}
