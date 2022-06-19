/*******************************************************************************
 * Copyright 2022 deathknight0718@qq.com.
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

import org.foliage.guava.common.collect.Maps;


/**
 * The common instance factory.
 *
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class InstanceFactory {

    // ------------------------------------------------------------------------

    private static InstanceProvider defaultProvider;

    private static Map<String, InstanceProvider> inheritedProviders = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    public static <T> T getInstance(Class<T> clazz) {
        return defaultProvider.getInstance(clazz);
    }

    public static <T> T getInstance(String bean, Class<T> clazz) {
        return defaultProvider.getInstance(bean, clazz);
    }

    public static <T> T getInstanceOfKey(String key, Class<T> clazz) {
        return inheritedProviders.get(key).getInstance(clazz);
    }

    public static <T> T getInstanceOfKey(String key, String bean, Class<T> clazz) {
        return inheritedProviders.get(key).getInstance(bean, clazz);
    }

    public static <T> Map<String, T> getInstancesOfType(Class<T> clazz) {
        return defaultProvider.getInstanceOfType(clazz);
    }

    public static Map<String, Object> getInstancesOfAnnotation(Class<? extends Annotation> clazz) {
        return defaultProvider.getInstanceOfAnnotation(clazz);
    }

    public static <T> Map<String, T> getInstancesOfKeyAndType(String key, Class<T> clazz) {
        return inheritedProviders.get(key).getInstanceOfType(clazz);
    }

    public static Map<String, Object> getInstancesOfKeyAndAnnotation(String key, Class<? extends Annotation> clazz) {
        return inheritedProviders.get(key).getInstanceOfAnnotation(clazz);
    }

    // ------------------------------------------------------------------------

    public static void defaultProvider(InstanceProvider provider) {
        defaultProvider = provider;
    }

    public static void inheritedProvider(String key, InstanceProvider provider) {
        inheritedProviders.put(key, provider);
    }

    // ------------------------------------------------------------------------

    public static void clean(String key) throws Exception {
        inheritedProviders.get(key).close();
        inheritedProviders.remove(key);
    }

    public static void cleanAll() throws Exception {
        if (defaultProvider != null) defaultProvider.close();
        for (Map.Entry<String, InstanceProvider> entry : inheritedProviders.entrySet()) {
            InstanceProvider provider = entry.getValue();
            if (provider != null) provider.close();
        }
        inheritedProviders.clear();
    }

}
