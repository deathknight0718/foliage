/*
 * Copyright 2023 Foliage Develop Team.
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
package page.foliage.flow;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleContext;
import org.flowable.common.engine.impl.de.odysseus.el.util.SimpleResolver;
import org.flowable.common.engine.impl.javax.el.ELContext;
import org.flowable.common.engine.impl.javax.el.ExpressionFactory;
import org.flowable.common.engine.impl.javax.el.PropertyNotFoundException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import page.foliage.common.ioc.InstanceFactory;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class JuelExpressionFactory {

    // ------------------------------------------------------------------------

    private final ExpressionFactory delegate;

    // ------------------------------------------------------------------------

    public JuelExpressionFactory(ExpressionFactory delegate) {
        this.delegate = delegate;
    }

    // ------------------------------------------------------------------------

    public static JuelExpressionFactory singleton() {
        return InstanceFactory.getInstance(JuelExpressionFactory.class);
    }

    // ------------------------------------------------------------------------

    public JuelExpression express(Map<String, Object> variables, InputStream input) {
        try (InputStream in = input) {
            return express(variables, IOUtils.toString(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JuelExpression express(Map<String, Object> variables, String text) {
        SimpleContext context = new SimpleContext(new Resolver());
        for (Map.Entry<String, Object> variable : variables.entrySet()) {
            context.setVariable(variable.getKey(), delegate.createValueExpression(variable.getValue(), variable.getValue().getClass()));
        }
        try {
            context.setFunction("foliage", "asJson", Functions.class.getMethod("asJson", Object.class));
            return new JuelExpression(context, delegate.createValueExpression(context, text, String.class));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public static class Functions {

        private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

        public static String asJson(Object value) {
            try {
                return MAPPER.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException(e);
            }
        }

    }

    // ------------------------------------------------------------------------

    public static class Resolver extends SimpleResolver {

        private Resolver() {
            super();
        }

        @Override
        public Object getValue(ELContext context, Object base, Object property) {
            try {
                return super.getValue(context, base, property);
            } catch (PropertyNotFoundException e) {
                return null;
            }
        }

    }

}
