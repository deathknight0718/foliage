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
package page.foliage.flow.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import page.foliage.flow.FlowDeployment;
import page.foliage.flow.TestBase;
import page.foliage.ldap.Access;

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

    @Test(enabled = true, priority = 1)
    private void testFlowDeploymentTenant01() {
        FlowDeployment deployment = FlowDeployment.builder(Access.current()) //
            .key("TestMultiTenant").name("共享合约") //
            .addResource("flows/202401150111.bpmn") //
            .addResource("flows/202401150117.bpmn") //
            .deploy();
        deployments.add(deployment);
        LOGGER.info("deployment: {}", deployment);
    }

    @Test(enabled = true, priority = 2)
    private void testFlowDeploymentTenant02() {
        FlowDeployment deployment = FlowDeployment.builder(Access.current()) //
            .key("TestMultiTenant").name("共享合约") //
            .addResource("flows/202401150111.bpmn") //
            .addResource("flows/202401150117.bpmn") //
            .deploy();
        deployments.add(deployment);
        LOGGER.info("deployment: {}", deployment);
    }

}
