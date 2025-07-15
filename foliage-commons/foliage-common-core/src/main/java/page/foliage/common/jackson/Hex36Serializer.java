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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import page.foliage.guava.common.base.Preconditions;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
@JacksonStdImpl
public class Hex36Serializer extends StdSerializer<Long> {

    public static final int HEX_BIT = 36;

    public final static Hex36Serializer instance = new Hex36Serializer();

    public Hex36Serializer() {
        super(Long.class);
    }

    public Hex36Serializer(Class<Long> handledType) {
        super(handledType);
    }

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(encode(value));
    }

    // Decodes a hexadecimal string representation of a long value.

    public static Long decode(String value) {
        Preconditions.checkNotNull(value, "Value to decode cannot be null");
        return Long.parseLong(value, HEX_BIT);
    }

    public static String encode(Long value) {
        Preconditions.checkNotNull(value, "Value to encode cannot be null");
        return Long.toString(value, HEX_BIT).toUpperCase();
    }

}
