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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.inject.Guice;
import page.foliage.inject.Injector;
import page.foliage.ldap.Access;

/**
 * 
 * @author deathknight0718@qq.com
 */
public abstract class TestBase {

    // ------------------------------------------------------------------------

    private static final Logger LOGGER = LoggerFactory.getLogger(TestBase.class);

    // ------------------------------------------------------------------------

    @BeforeSuite
    public void beforeSuite() throws Exception {
        LOGGER.info("TestBase.beforeClass() called");
        Injector injector = Guice.createInjector(new TestModuleJdbc(), new TestModuleLdap(), new TestModuleFlow());
        InstanceFactory.provide(new InstanceGuice(injector));
        Access.register(20250709143301L);
    }

}
