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
import page.foliage.common.jackson.FoliageModule;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class JsonNodes {

    // ------------------------------------------------------------------------

    static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new FoliageModule());

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

    public static ObjectNode asObject(String value) throws JsonProcessingException {
        return (ObjectNode) asNode(value);
    }

    public static ObjectNode asObject(byte[] bytes) throws IOException {
        return (ObjectNode) asNode(bytes);
    }

    public static ObjectNode asObject(InputStream is) throws IOException {
        return (ObjectNode) asNode(is);
    }

    // ------------------------------------------------------------------------

    public static ArrayNode asArray(float[] values) {
        ArrayNode node = createArrayNode();
        for (float value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(double[] values) {
        ArrayNode node = createArrayNode();
        for (double value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(boolean[] values) {
        ArrayNode node = createArrayNode();
        for (boolean value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(byte[] values) {
        ArrayNode node = createArrayNode();
        for (byte value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(char[] values) {
        ArrayNode node = createArrayNode();
        for (char value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(short[] values) {
        ArrayNode node = createArrayNode();
        for (short value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(int[] values) {
        ArrayNode node = createArrayNode();
        for (int value : values) {
            node.add(value);
        }
        return node;
    }

    public static ArrayNode asArray(long[] values) {
        ArrayNode node = createArrayNode();
        for (long value : values) {
            node.add(value);
        }
        return node;
    }

    // ------------------------------------------------------------------------

    public static JsonNode asNode(String value) throws JsonProcessingException {
        return MAPPER.readTree(value);
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
