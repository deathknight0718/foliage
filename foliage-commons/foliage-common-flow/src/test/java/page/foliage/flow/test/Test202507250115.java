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
package page.foliage.flow.test;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import page.foliage.common.collect.Identities;
import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.util.JsonNodes;
import page.foliage.flow.FlowDefinition;
import page.foliage.flow.FlowProcess;
import page.foliage.flow.FlowTask;
import page.foliage.flow.TestBase;
import page.foliage.flow.bean.MockDefinition;
import page.foliage.flow.bean.MockDeployment;
import page.foliage.ldap.Access;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class Test202507250115 extends TestBase {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(Test202507250115.class);

    private FlowDefinition definition;

    private FlowProcess process;

    // ------------------------------------------------------------------------

    @Test
    public void testDeployment() {
        InstanceFactory.getInstance(MockDeployment.class).f202507250115().deploy();
    }

    @Test(dependsOnMethods = "testDeployment")
    public void testDefinition() {
        definition = InstanceFactory.getInstance(MockDefinition.class).d202507250115();
    }

    @Test(dependsOnMethods = "testDefinition")
    public void testProcess() throws JsonProcessingException {
        process = definition.starter().name("Test202507250115").referenceId(Identities.snowflake()) //
            .referenceType("APPOINTMENT").variable("test", "01").start();
        LOGGER.info(JsonNodes.format(process));
    }

    @Test(dependsOnMethods = "testProcess")
    public void testTask() throws InterruptedException {
        Thread.sleep(50 * 1000);
        List<FlowTask> tasks = FlowTask.list(Access.current(), QueryParams.ALL);
        Assert.assertFalse(tasks.isEmpty());
        FlowTask task = tasks.iterator().next();
        Assert.assertEquals(task.getName(), "免疫门诊");
        LOGGER.info("Task: {}, Name: {}, Variables: {}, State: {}", task.getId(), task.getName(), task.variables(), task.getDelegationState());
    }

}
