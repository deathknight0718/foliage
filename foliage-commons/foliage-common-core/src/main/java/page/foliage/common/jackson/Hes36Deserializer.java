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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import page.foliage.common.util.CodecUtils;

/**
 *
 *
 * @author: liuzheng@cecdat.com
 */
public class Hes36Deserializer extends StdDeserializer<Long> {

    private static final long serialVersionUID = 1L;

    public static final int HEX_BIT = 36;

    public static final Hes36Deserializer instance = new Hes36Deserializer();

    public Hes36Deserializer() {
        super(String.class);
    }

    public Hes36Deserializer(Class<Long> clazz) {
        super(clazz);
    }

    @Override
    public Long deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        return CodecUtils.decodeHex36(parser.readValueAs(String.class));
    }

}
