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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import page.foliage.common.util.DateTimes;
import page.foliage.guava.common.collect.Range;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class RangeDateTimeSerializer extends StdSerializer<Range<LocalDateTime>> {

    private static final long serialVersionUID = 1L;

    public static final RangeDateTimeSerializer instance = new RangeDateTimeSerializer();

    @SuppressWarnings("unchecked")
    public RangeDateTimeSerializer() {
        super((Class<Range<LocalDateTime>>) (Class<?>) Range.class);
    }

    public RangeDateTimeSerializer(Class<Range<LocalDateTime>> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(Range<LocalDateTime> value, JsonGenerator generator, SerializerProvider provider) throws IOException, JsonProcessingException {
        generator.writeStartObject();
        generator.writeFieldName("lower");
        generator.writeString(DateTimes.format(value.lowerEndpoint()));
        generator.writeFieldName("upper");
        generator.writeString(DateTimes.format(value.upperEndpoint()));
        generator.writeEndObject();
    }

}