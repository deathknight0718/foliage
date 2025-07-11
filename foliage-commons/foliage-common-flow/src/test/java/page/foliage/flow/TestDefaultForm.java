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
public class TestDefaultForm extends TestBase {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDefaultForm.class);

    private static List<FormResource> resources = Lists.newArrayList();

    // ------------------------------------------------------------------------

    @AfterClass
    private static void afterClass() {
        for (FormResource resource : resources) {
            resource.remove();
        }
    }

    // ------------------------------------------------------------------------

    @Test(enabled = true)
    private void testDefaultForm() {
        FormResource resource1 = FormResource.defaultIfAbset(Domain.SYSTEM);
        resources.add(resource1);
        LOGGER.info("form resource: {}", resource1);
        FormResource resource2 = FormResource.defaultIfAbset(Domain.get("domain1"));
        resources.add(resource2);
        LOGGER.info("form resource: {}", resource2);
    }

}
