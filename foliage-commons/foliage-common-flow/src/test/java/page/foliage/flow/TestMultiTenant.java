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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import page.foliage.ldap.Domain;
import page.foliage.test.TestBase;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestMultiTenant extends TestBase {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMultiTenant.class);

    private static List<FlowDeployment> deployments = Lists.newArrayList();

    // ------------------------------------------------------------------------

    @AfterClass
    private static void afterClass() {
        for (FlowDeployment deployment : deployments) {
            deployment.remove();
        }
    }

    // ------------------------------------------------------------------------

    @Test(enabled = true, priority = 1)
    private void testFlowDeploymentTenant01() {
        FlowDeployment deployment = FlowDeployment.builder(Domain.SYSTEM) //
            .key("TestMultiTenant").name("共享合约") //
            .addResource("flows/202401150111.bpmn") //
            .addResource("flows/202401150117.bpmn") //
            .deploy();
        deployments.add(deployment);
        LOGGER.info("deployment: {}", deployment);
    }

    @Test(enabled = true, priority = 2)
    private void testFlowDeploymentTenant02() {
        FlowDeployment deployment = FlowDeployment.builder(Domain.get("domain1")) //
            .key("TestMultiTenant").name("共享合约") //
            .addResource("flows/202401150111.bpmn") //
            .addResource("flows/202401150117.bpmn") //
            .deploy();
        deployments.add(deployment);
        LOGGER.info("deployment: {}", deployment);
    }

    @Test(enabled = true, priority = 3)
    private void testFlowDefinitionKey() {
        FlowDefinition definition1 = FlowDefinition.latest("repository-contract-submit", Domain.SYSTEM);
        LOGGER.info("definition: {}, key={}, tenant={}", definition1, definition1.getKey(), definition1.getTenantId());
        FlowDefinition definition2 = FlowDefinition.latest("repository-contract-signed", Domain.SYSTEM);
        LOGGER.info("definition: {}, key={}, tenant={}", definition2, definition2.getKey(), definition2.getTenantId());
    }

}
