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
package page.foliage.common.ioc;

import java.lang.annotation.Annotation;

import jakarta.enterprise.util.TypeLiteral;
import page.foliage.common.annotation.Specialized;

/**
 * The common instance factory.
 *
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class InstanceFactory {

    // ------------------------------------------------------------------------

    private static InstanceProvider provider;

    // ------------------------------------------------------------------------

    public static <T> T getInstance(Class<T> clazz) {
        return provider.getInstance(clazz);
    }

    public static <T> T getInstance(Class<T> clazz, Annotation annotation) {
        return provider.getInstance(clazz, annotation);
    }

    public static <T> T getInstanceComposited(Class<T> clazz) {
        return provider.getInstanceComposited(clazz);
    }

    public static <T> T getInstanceSpecialized(Class<T> clazz) {
        return provider.getInstance(clazz, Specialized.Literal.INSTANCE);
    }

    public static <T> T getInstance(Class<T> clazz, String name) {
        return provider.getInstance(clazz, name);
    }

    public static <T> T getInstanceLiteral(TypeLiteral<T> typeLiteral) {
        return provider.getInstanceLiteral(typeLiteral);
    }

    // ------------------------------------------------------------------------

    public static void provide(InstanceProvider provider) {
        InstanceFactory.provider = provider;
    }

}
