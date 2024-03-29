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
package page.foliage.common.util;

import page.foliage.guava.common.base.Preconditions;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class ReflectionUtils {

    @SuppressWarnings("unchecked")
    public static <T> T checkInstance(Class<T> clazz, Object obj) {
        Preconditions.checkArgument(clazz.isInstance(obj), "Error! cannot cast to %s with %s", clazz.getName(), obj);
        return (T) obj;
    }

    @SuppressWarnings("unchecked")
    public static <T> T checkInstance(Class<T> clazz, Object obj, String pattern, Object... arguments) {
        Preconditions.checkArgument(clazz.isInstance(obj), "Error! cannot cast to %s with %s", clazz.getName(), obj, pattern, arguments);
        return (T) obj;
    }

}
