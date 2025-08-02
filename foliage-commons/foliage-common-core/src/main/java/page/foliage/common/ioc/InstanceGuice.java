/*******************************************************************************
 * Copyright 2022 CEC Data Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package page.foliage.common.ioc;

import java.lang.annotation.Annotation;

import page.foliage.common.annotation.Composited;
import page.foliage.common.annotation.Specialized;
import page.foliage.inject.AbstractModule;
import page.foliage.inject.Guice;
import page.foliage.inject.Injector;
import page.foliage.inject.Key;
import page.foliage.inject.name.Names;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class InstanceGuice implements InstanceProvider {

    // ------------------------------------------------------------------------

    private final Injector injector;

    // ------------------------------------------------------------------------

    public InstanceGuice(Injector injector) {
        this.injector = injector;
    }

    // ------------------------------------------------------------------------

    public static InstanceProvider withModule(AbstractModule... modules) {
        return new InstanceGuice(Guice.createInjector(modules));
    }

    // ------------------------------------------------------------------------

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public <T> T getInstance(Class<T> clazz, Annotation annotation) {
        return injector.getInstance(Key.get(clazz, annotation));
    }

    @Override
    public <T> T getInstanceComposited(Class<T> clazz) {
        return injector.getInstance(Key.get(clazz, Composited.Literal.INSTANCE));
    }

    @Override
    public <T> T getInstanceSpecialized(Class<T> clazz) {
        return injector.getInstance(Key.get(clazz, Specialized.Literal.INSTANCE));
    }

    @Override
    public <T> T getInstance(Class<T> clazz, String name) {
        return injector.getInstance(Key.get(clazz, Names.named(name)));
    }

}
