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

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import page.foliage.common.collect.QueryParams;
import page.foliage.ldap.Access;
import page.foliage.ldap.Domain;
import page.foliage.ldap.StatelessAccess;
import page.foliage.test.TestBase;

/**
 * 
 * @author deathknight0718@qq.com
 */
@Test
public class TestDataContractWithForm extends TestBase {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataContractWithForm.class);

    private static final Boolean WILL_BE_REMOVE = Boolean.TRUE;

    private static final FormResource[] RESOURCES = new FormResource[4];

    private static final FlowDeployment[] DEPLOYMENTS = new FlowDeployment[4];

    private static final String[] EMAILS = new String[] { "liuzheng@cecdat.com", "domain2admin1@cecdat.com" };

    private static final String[][] PARAMS = new String[][] { //
        { "contract-submit", "共享合约 - 流程发起", "rjsfs/202401121600.rjsf" }, //
        { "contract-signed", "共享合约 - 分配仓库", "rjsfs/202401121653.rjsf" }, //
        { "repository-contract-submit", "共享合约 - 流程发起", "flows/202401150111.bpmn" }, //
        { "repository-contract-signed", "共享合约 - 分配仓库", "flows/202401150117.bpmn" }, //
    };

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() {
        TestBase.beforeClass();
        FederatedEngine.singleton();
        RESOURCES[0] = buildFormResource(EMAILS[0], PARAMS[0][0], PARAMS[0][1], PARAMS[0][2]);
        RESOURCES[1] = buildFormResource(EMAILS[0], PARAMS[1][0], PARAMS[1][1], PARAMS[1][2]);
        RESOURCES[2] = buildFormResource(EMAILS[1], PARAMS[0][0], PARAMS[0][1], PARAMS[0][2]);
        RESOURCES[3] = buildFormResource(EMAILS[1], PARAMS[1][0], PARAMS[1][1], PARAMS[1][2]);
        DEPLOYMENTS[0] = buildFlowResource(EMAILS[0], PARAMS[2][0], PARAMS[2][1], PARAMS[2][2]);
        DEPLOYMENTS[1] = buildFlowResource(EMAILS[0], PARAMS[3][0], PARAMS[3][1], PARAMS[3][2]);
        DEPLOYMENTS[2] = buildFlowResource(EMAILS[1], PARAMS[2][0], PARAMS[2][1], PARAMS[2][2]);
        DEPLOYMENTS[3] = buildFlowResource(EMAILS[1], PARAMS[3][0], PARAMS[3][1], PARAMS[3][2]);
    }

    @AfterClass
    public static void afterClass() {
        if (!WILL_BE_REMOVE) return;
        Arrays.asList(DEPLOYMENTS).forEach(FlowDeployment::remove);
        Arrays.asList(RESOURCES).forEach(FormResource::remove);
    }

    // ------------------------------------------------------------------------

    private static FormResource buildFormResource(String email, String key, String name, String classpath) {
        return FormResource.builder().key(key).name(name).access(StatelessAccess.fromEmail(email)).resource(classpath).build();
    }

    private static FlowDeployment buildFlowResource(String email, String key, String name, String classpath) {
        return FlowDeployment.builder(StatelessAccess.fromEmail(email).getDomain()).key(key).name(name).addResource(classpath).deploy();
    }

    // ------------------------------------------------------------------------

    @Test(enabled = true)
    private void testFlowProcessStart() throws InterruptedException {
        Access access = StatelessAccess.fromEmail(EMAILS[1]);
        FlowDefinition definition = FlowDefinition.latest(PARAMS[2][0], access.getDomain());
        definition.starter(access.getUser()).name("TestDataContract").payload(RESOURCES[2].express(access)).start();
    }

    @Test(enabled = true, dependsOnMethods = { "testFlowProcessStart" })
    private void testFlowUserTaskSubmit01() throws InterruptedException {
        Access access = StatelessAccess.fromEmail(EMAILS[1]);
        FlowTask task = FlowTask.list(QueryParams.ALL, access.getUser()).iterator().next();
        Assert.assertEquals(task.getName(), "内容审查");
        task.submitter(access.getUser()).payload(task.resource().express(access)).complete();
    }

    @Test(enabled = true, dependsOnMethods = { "testFlowUserTaskSubmit01" })
    private void testFlowUserTaskSubmit02() throws InterruptedException {
        Access access = StatelessAccess.fromEmail(EMAILS[0]);
        FlowTask task = FlowTask.list(QueryParams.ALL, Domain.SYSTEM).iterator().next();
        Assert.assertEquals(task.getName(), "合约审查");
        task.submitter(access.getUser()).payload(RESOURCES[1].express(access)) //
            .variable("repositoryId", Long.valueOf(800001001L)).variable("response", Boolean.TRUE) //
            .complete();
    }

    @Test(enabled = true, dependsOnMethods = { "testFlowUserTaskSubmit02" })
    private void testFlowResult() throws InterruptedException {
        LOGGER.info("Successful");
    }

}
