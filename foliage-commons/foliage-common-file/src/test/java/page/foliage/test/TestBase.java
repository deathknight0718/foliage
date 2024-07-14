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
package page.foliage.test;

import org.testng.annotations.BeforeClass;

import page.foliage.common.ioc.InstanceClosingCheck;
import page.foliage.common.ioc.InstanceFactory;
import page.foliage.common.ioc.InstanceGuice;
import page.foliage.file.session.MinioSessionFactory;
import page.foliage.file.session.impl.MinioSessionFactoryImpl;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.inject.Singleton;

/**
 * 
 * @author deathknight0718@qq.com
 */
public abstract class TestBase {

    // ------------------------------------------------------------------------

    private static String endpoint = "https://portal.cecdat.dev:9201";

    private static String[] keys = new String[] { "DhnJSgMNuL58zb1dUQss", "5SbOYQbeSq5eTQBY7cKLRbaxpTnRdL0Am1zvZR0x" };

    // ------------------------------------------------------------------------

    @BeforeClass
    public static void beforeClass() throws Exception {
        InstanceFactory.provide(InstanceGuice.withModule(new InternalModule()));
    }

    // ------------------------------------------------------------------------

    public static class InternalModule extends AbstractModule {

        @Provides
        @Singleton
        private MinioSessionFactory buildMinioSessionFactory() {
            MinioSessionFactory bean = MinioSessionFactoryImpl.builder() //
                .endpoint(endpoint).credentials(keys[0], keys[1]).build();
            return InstanceClosingCheck.hook(bean);
        }

    }

}
