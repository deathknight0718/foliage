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
package page.foliage.common.jackson;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import page.foliage.common.util.DateTimes;
import page.foliage.guava.common.collect.Range;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class RangeDateTimeDeserializer extends StdDeserializer<Range<LocalDateTime>> {

    private static final long serialVersionUID = 1L;

    public static final int HEX_BIT = 36;

    public static final RangeDateTimeDeserializer instance = new RangeDateTimeDeserializer();

    @SuppressWarnings("unchecked")
    public RangeDateTimeDeserializer() {
        super((Class<Range<LocalDateTime>>) (Class<?>) Range.class);
    }

    public RangeDateTimeDeserializer(Class<Range<LocalDateTime>> clazz) {
        super(clazz);
    }

    @Override
    public Range<LocalDateTime> deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectNode node = (ObjectNode) parser.readValueAsTree();
        if (!node.has("lower") || !node.has("upper")) throw new JsonProcessingException("Missing required fields: lower or upper") {};
        try {
            LocalDateTime lower = DateTimes.dateTimeOf(node.get("lower").asText());
            LocalDateTime upper = DateTimes.dateTimeOf(node.get("upper").asText());
            return Range.closed(lower, upper);
        } catch (Exception e) {
            throw new JsonProcessingException("Error decoding Range<LocalDateTime>: " + e.getMessage(), e) {};
        }
    }

}
