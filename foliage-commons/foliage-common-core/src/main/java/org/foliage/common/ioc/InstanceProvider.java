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

/**
 * The instance provider interface.
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public interface InstanceProvider extends AutoCloseable {

    <T> T getInstance(Class<T> clazz);

    <T> T getInstance(Class<T> clazz, Object... arguments);

    <T> T getInstance(String bean, Class<T> clazz);

    <T> Map<String, T> getInstanceOfType(Class<T> clazz);

    Map<String, Object> getInstanceOfAnnotation(Class<? extends Annotation> clazz);

}
