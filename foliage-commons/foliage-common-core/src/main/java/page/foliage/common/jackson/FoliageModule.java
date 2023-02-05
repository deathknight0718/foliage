/*******************************************************************************
 * Copyright 2020 Greatwall Inc.
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
package page.foliage.common.jackson;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

/**
 * 
 * 
 * @author 1111395@greatwall.com.cn
 * @version 1.0.0
 */
public class FoliageModule extends Module {

    @Override
    public String getModuleName() {
        return "FoliageModule";
    }

    @Override
    public Version version() {
        return Version.unknownVersion();
    }

    @Override
    public void setupModule(SetupContext context) {
        SimpleSerializers serializers = new SimpleSerializers();
        SimpleDeserializers deserializers = new SimpleDeserializers();
        serializers.addSerializer(OffsetDateTime.class, new OffsetDateTimeSerializer());
        serializers.addSerializer(ZonedDateTime.class, new ZonedDateTimeSerializer());
        serializers.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        serializers.addSerializer(LocalDate.class, new LocalDateSerializer());
        deserializers.addDeserializer(OffsetDateTime.class, new OffsetDateTimeDeserializer());
        deserializers.addDeserializer(ZonedDateTime.class, new ZonedDateTimeDeserializer());
        deserializers.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        deserializers.addDeserializer(LocalDate.class, new LocalDateDeserializer());
        context.addSerializers(serializers);
        context.addDeserializers(deserializers);
    }

}
