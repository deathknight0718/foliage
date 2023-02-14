/*
 * Copyright 2022 Deathknight0718.
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
package page.foliage.common.ioc;

import org.springframework.context.ApplicationContext;

import page.foliage.common.util.ResourceUtils;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class InstanceSpring implements InstanceProvider, AutoCloseable {

    // -------------------------------------------------------------- ATTRIBUTE

    private final ApplicationContext context;

    // ------------------------------------------------------------ CONSTRUCTOR

    public InstanceSpring(ApplicationContext context) {
        this.context = context;
    }

    // ----------------------------------------------------------------- METHOD

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return context.getBean(clazz);
    }

    // ----------------------------------------------------------------- METHOD

    @Override
    public void close() throws Exception {
        ResourceUtils.safeClose(context);
    }

}
