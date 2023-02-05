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

import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author deathknight0718@qq.com.
 * @version 1.0.0
 */
public class ResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    public static InputStream readResource(String path) throws IOException {
        return readResource(path, ClassLoader.getSystemClassLoader());
    }

    public static InputStream readResource(String path, ClassLoader loader) throws IOException {
        return loader.getResourceAsStream(path);
    }

    public static String readResourceToString(String path) throws IOException {
        return readResourceToString(path, ClassLoader.getSystemClassLoader());
    }

    public static String readResourceToString(String path, ClassLoader loader) throws IOException {
        return IOUtils.toString(loader.getResourceAsStream(path), StandardCharsets.UTF_8);
    }

    public static void safeClose(Object... objects) {
        if (objects == null || objects.length == 0) {
            logger.info("safeClose(...) was invoked with null or empty array: {}", objects);
            return;
        }
        for (Object obj : objects) {
            if (obj != null) {
                logger.debug("Trying to safely close {}", obj);
                if (obj instanceof Flushable) {
                    try {
                        ((Flushable) obj).flush();
                    } catch (Exception e) {
                        logger.debug("Flushing Flushable failed", e);
                    }
                }
                if (obj instanceof AutoCloseable) {
                    try {
                        ((AutoCloseable) obj).close();
                    } catch (Exception e) {
                        logger.debug("Closing AutoCloseable failed", e);
                    }
                } else {
                    logger.info("obj was not AutoCloseable, trying to find close() method via reflection.");
                    try {
                        Method method = obj.getClass().getMethod("close", new Class[0]);
                        if (method == null) {
                            logger.info("obj did not have a close() method, ignoring");
                        } else {
                            method.setAccessible(true);
                            method.invoke(obj);
                        }
                    } catch (InvocationTargetException e) {
                        logger.warn("Invoking close() by reflection threw exception", e);
                    } catch (Exception e) {
                        logger.warn("Could not invoke close() by reflection", e);
                    }
                }
            }

        }
    }

}
