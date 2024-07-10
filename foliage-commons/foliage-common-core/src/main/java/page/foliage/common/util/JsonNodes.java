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
package page.foliage.common.util;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class JsonNodes {

    // ------------------------------------------------------------------------

    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    // ------------------------------------------------------------------------

    public static ArrayNode createArrayNode() {
        return MAPPER.createArrayNode();
    }

    public static ObjectNode createObjectNode() {
        return MAPPER.createObjectNode();
    }

    // ------------------------------------------------------------------------

    public static <T> T asType(String value, TypeReference<T> typeReference) throws JsonProcessingException {
        return MAPPER.readValue(value, typeReference);
    }

    public static <T> T asType(JsonNode value, TypeReference<T> typeReference) throws JsonProcessingException {
        return MAPPER.convertValue(value, typeReference);
    }

    // ------------------------------------------------------------------------

    public static JsonNode asNode(String json) throws JsonProcessingException {
        return MAPPER.readTree(json);
    }

    public static JsonNode asNode(byte[] bytes) throws IOException {
        return MAPPER.readTree(bytes);
    }

    public static JsonNode asNode(InputStream is) throws IOException {
        return MAPPER.readTree(is);
    }

    public static <T extends JsonNode> T toNode(Object value) {
        return MAPPER.valueToTree(value);
    }

    public static String format(Object value) throws JsonProcessingException {
        return MAPPER.writeValueAsString(value);
    }

}
