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
package page.foliage.flow;

import javax.sql.DataSource;

import org.flowable.common.engine.impl.de.odysseus.el.ExpressionFactoryImpl;

import page.foliage.inject.AbstractModule;
import page.foliage.inject.Provides;
import page.foliage.inject.Singleton;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class TestModuleFlow extends AbstractModule {

    @Provides
    @Singleton
    public FederatedEngine buildFederatedEngine(DataSource dataSource) {
        return new FederatedEngineBuilder().withAsyncExecutorActivate(true).withDataSource(dataSource).build();
    }

    @Provides
    @Singleton
    public JuelExpressionFactory buildJuelExpressionFactory() {
        return new JuelExpressionFactory(new ExpressionFactoryImpl());
    }

}
