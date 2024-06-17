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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class InstanceClosingCheck extends Thread {

    private final static Logger LOGGER = LoggerFactory.getLogger(InstanceClosingCheck.class);

    private final AutoCloseable closeable;

    public InstanceClosingCheck(AutoCloseable closeable) {
        this.closeable = closeable;
    }

    @Override
    public void run() {
        if (closeable == null) return;
        try {
            LOGGER.info("Safe Closing Resource: {}", closeable);
            closeable.close();
        } catch (Exception e) {
            LOGGER.debug("Closing AutoCloseable failed", e);
        }
    }

    public static <T extends AutoCloseable> T hook(T instance) {
        Runtime.getRuntime().addShutdownHook(new InstanceClosingCheck(instance));
        return instance;
    }

}
