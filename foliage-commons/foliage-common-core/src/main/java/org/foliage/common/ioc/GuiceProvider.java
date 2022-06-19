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
package org.foliage.common.ioc;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class GuiceProvider implements InstanceProvider {

    // ------------------------------------------------------------------------

    private final Injector injector;

    // ------------------------------------------------------------------------

    private GuiceProvider(Injector injector) {
        this.injector = injector;
    }

    // ------------------------------------------------------------------------

    public static InstanceProvider withModule(AbstractModule... modules) {
        return new GuiceProvider(Guice.createInjector(modules));
    }

    // ------------------------------------------------------------------------

    @Override
    public void close() throws Exception {
        // DONOTHING
    }

    // ------------------------------------------------------------------------

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return injector.getInstance(clazz);
    }

    @Override
    public <T> T getInstance(Class<T> clazz, Object... arguments) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getInstance(String bean, Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Map<String, T> getInstanceOfType(Class<T> clazz) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getInstanceOfAnnotation(Class<? extends Annotation> clazz) {
        throw new UnsupportedOperationException();
    }

}
