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

import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import page.foliage.common.util.JsonNodes;
import page.foliage.guava.common.collect.ForwardingMap;
import page.foliage.guava.common.collect.ImmutableMap;
import page.foliage.guava.common.collect.Maps;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class FlowVariables extends ForwardingMap<String, Object> {

    // ------------------------------------------------------------------------

    public static final String KEY_ASSIGNEE_ID = "_assigneeId";

    public static final String KEY_ACCESS_ID = "_accessId";

    public static final String KEY_REFERENCE_ID = "_referenceId";

    public static final String KEY_REFERENCE_TYPE = "_referenceType";

    public static final String KEY_RESULT = "_result";

    public static final String KEY_RESULT_REASON = "_resultReason";

    public static final String KEY_RESULT_REFERENCE_ID = "_resultReferenceId";

    // ------------------------------------------------------------------------

    public static final FlowVariables EMPTY = new FlowVariables(ImmutableMap.of());

    public static final TypeReference<Map<String, Object>> TYPE_REFERENCE = new TypeReference<>() {};

    // ------------------------------------------------------------------------

    private final Map<String, Object> delegate;

    // ------------------------------------------------------------------------

    public FlowVariables() {
        this.delegate = Maps.newHashMap();
    }

    public FlowVariables(Map<String, Object> input) {
        this.delegate = Maps.newHashMap(input);
    }

    // ------------------------------------------------------------------------

    public static FlowVariables of(JsonNode input) {
        try {
            return new FlowVariables(JsonNodes.asType(input, TYPE_REFERENCE));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static FlowVariables of(String k1, Object v1) {
        FlowVariables variables = new FlowVariables();
        variables.put(k1, v1);
        return variables;
    }

    public static FlowVariables of(String k1, Object v1, String k2, Object v2) {
        FlowVariables variables = new FlowVariables();
        variables.put(k1, v1);
        variables.put(k2, v2);
        return variables;
    }

    public static FlowVariables of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        FlowVariables variables = new FlowVariables();
        variables.put(k1, v1);
        variables.put(k2, v2);
        variables.put(k3, v3);
        return variables;
    }

    public static FlowVariables of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        FlowVariables variables = new FlowVariables();
        variables.put(k1, v1);
        variables.put(k2, v2);
        variables.put(k3, v3);
        variables.put(k4, v4);
        return variables;
    }

    // ------------------------------------------------------------------------

    public OptionalValue val(String key) {
        return new OptionalValue(Optional.ofNullable(get(key)));
    }

    // ------------------------------------------------------------------------

    @Override
    protected Map<String, Object> delegate() {
        return delegate;
    }

    // ------------------------------------------------------------------------

    public static class OptionalValue {

        private final Optional<Object> value;

        public OptionalValue(Optional<Object> value) {
            this.value = value;
        }

        public boolean isNull() {
            return !value.isPresent();
        }

        public Long asLong() {
            return value.map(Object::toString).map(Long::valueOf).get();
        }

        public String asText() {
            return value.get().toString();
        }

    }

}
