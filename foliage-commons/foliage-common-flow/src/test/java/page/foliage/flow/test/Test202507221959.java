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

import org.testng.Assert;
import org.testng.annotations.Test;

import page.foliage.common.collect.QueryParams;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.flow.FlowDefinition;
import page.foliage.flow.FlowHistoricActivity;
import page.foliage.flow.FlowProcess;
import page.foliage.flow.TestBase;
import page.foliage.flow.bean.MockDefinition;
import page.foliage.flow.bean.MockDeployment;
import page.foliage.ldap.Access;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class Test202507221959 extends TestBase {

    // ------------------------------------------------------------------------

    private FlowDefinition definition;

    // ------------------------------------------------------------------------

    @Test
    public void testDeployment() {
        InstanceFactory.getInstance(MockDeployment.class).f202507221959().deploy();
    }

    @Test(dependsOnMethods = "testDeployment")
    public void testDefinition() {
        definition = InstanceFactory.getInstance(MockDefinition.class).d202507221959();
    }

    @Test(dependsOnMethods = "testDefinition")
    public void testProcess() {
        FlowProcess.builder(definition).variable("test", "01").start();
    }

    @Test(dependsOnMethods = "testProcess")
    public void testTask() {
        List<FlowHistoricActivity> activities = FlowHistoricActivity.list(Access.current(), QueryParams.ALL);
        Assert.assertFalse(activities.isEmpty());
    }

}
