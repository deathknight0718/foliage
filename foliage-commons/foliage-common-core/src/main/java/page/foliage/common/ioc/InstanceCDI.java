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

import java.lang.annotation.Annotation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.NamedLiteral;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.TypeLiteral;
import page.foliage.common.annotation.Composited;
import page.foliage.common.annotation.Specialized;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class InstanceCDI implements InstanceProvider {

    // ------------------------------------------------------------------------

    private static volatile InstanceCDI singleton;

    private static final Logger LOGGER = LoggerFactory.getLogger(InstanceCDI.class);

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
        Instance<T> instance = CDI.current().select(clazz);
        if (instance.isAmbiguous()) {
            LOGGER.debug("Instance of {} is ambiguous, selecting specialized instance.", clazz.getName());
            instance = instance.select(Specialized.Literal.INSTANCE);
        }
        if (instance.isUnsatisfied()) {
            LOGGER.debug("Instance of {} is unsatisfied, selecting specialized instance.", clazz.getName());
            instance = instance.select(Specialized.Literal.INSTANCE);
        }
        return instance.get();
    }

    @Override
    public <T> T getInstance(Class<T> clazz, Annotation annotation) {
        return CDI.current().select(clazz).select(annotation).get();
    }

    @Override
    public <T> T getInstanceComposited(Class<T> clazz) {
        return CDI.current().select(clazz).select(Composited.Literal.INSTANCE).get();
    }

    @Override
    public <T> T getInstanceSpecialized(Class<T> clazz) {
        return CDI.current().select(clazz).select(Specialized.Literal.INSTANCE).get();
    }

    @Override
    public <T> T getInstance(Class<T> clazz, String name) {
        return CDI.current().select(clazz).select(NamedLiteral.of(name)).get();
    }

    @Override
    public <T> T getInstanceLiteral(TypeLiteral<T> typeLiteral) {
        Instance<T> instance = CDI.current().select(typeLiteral);
        if (instance.isAmbiguous()) {
            LOGGER.debug("Instance of {} is ambiguous, selecting specialized instance.", typeLiteral.getType().getTypeName());
            instance = instance.select(Specialized.Literal.INSTANCE);
        }
        if (instance.isUnsatisfied()) {
            LOGGER.debug("Instance of {} is unsatisfied, selecting specialized instance.", typeLiteral.getType().getTypeName());
            instance = instance.select(Specialized.Literal.INSTANCE);
        }
        return instance.get();
    }

}
