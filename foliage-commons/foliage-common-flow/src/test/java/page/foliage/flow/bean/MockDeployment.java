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
package page.foliage.flow.bean;

import page.foliage.flow.FlowDeployment;
import page.foliage.ldap.Access;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class MockDeployment {

    public FlowDeployment.Builder f202507221959() {
        FlowDeployment.Builder builder = FlowDeployment.builder(Access.current());
        builder.key("Test202507221959").name("基础流程");
        builder.addResource("flows/202507221959.bpmn");
        return builder;
    }

    public FlowDeployment.Builder f202507250115() {
        FlowDeployment.Builder builder = FlowDeployment.builder(Access.current());
        builder.key("Test202507250115").name("免疫门诊流程");
        builder.addResource("flows/202507250115.bpmn");
        return builder;
    }

}
