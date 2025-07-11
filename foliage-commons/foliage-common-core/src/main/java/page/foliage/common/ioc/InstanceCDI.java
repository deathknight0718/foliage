/*
 * Copyright 2024 Foliage Develop Team.
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

import jakarta.enterprise.inject.spi.CDI;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class InstanceCDI implements InstanceProvider {

    // ------------------------------------------------------------------------

    private static volatile InstanceCDI singleton;

    // ------------------------------------------------------------------------

    public static InstanceCDI singleton() {
        InstanceCDI result = singleton;
        if (result == null) {
            synchronized (InstanceCDI.class) {
                result = singleton;
                if (result == null) {
                    singleton = result = new InstanceCDI();
                }
            }
        }
        return result;
    }

    // ------------------------------------------------------------------------

    @Override
    public <T> T getInstance(Class<T> clazz) {
        return CDI.current().select(clazz).get();
    }

}
