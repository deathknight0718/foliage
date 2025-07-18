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
package page.foliage.ldap;

import java.util.function.Supplier;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class UserThreadLocal {

    // ------------------------------------------------------------------------

    public static final String TYPE_KEY_OF_USER_ID = "FoliageUserId";

    private static final ThreadLocal<Long> THREAD_LOCAL = ThreadLocal.withInitial(() -> null);

    // ------------------------------------------------------------------------

    public static Long get() {
        return THREAD_LOCAL.get();
    }

    public static void set(Long value) {
        THREAD_LOCAL.set(value);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static boolean exists() {
        return get() != null;
    }

    public static <T> T withTemporary(Long current, Supplier<T> operation) {
        Long original = get();
        try {
            set(current);
            return operation.get();
        } finally {
            if (original != null) set(original);
            else remove();
        }
    }

}
