/*
 * Copyright 2022 Deathknight0718.
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
package page.foliage.oidc;

import java.time.Instant;
import java.util.Base64;
import java.util.Base64.Decoder;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author deathknight0718@qq.com
 */
public class OidcToken {

    // ------------------------------------------------------------------------

    private final static ObjectMapper MAPPER = new ObjectMapper();

    // ------------------------------------------------------------------------

    private JsonNode payload;

    private String content;

    // ------------------------------------------------------------------------

    private OidcToken() {}

    // ------------------------------------------------------------------------

    public static OidcToken of(String input) {
        OidcToken token = new OidcToken();
        token.content = input;
        try {
            Decoder decoder = Base64.getDecoder();
            String segment = StringUtils.split(token.content, ".")[1];
            token.payload = MAPPER.readTree(decoder.decode(segment));
            return token;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    // ------------------------------------------------------------------------

    public boolean isExpired() {
        return Instant.now().getEpochSecond() > payload.path("exp").longValue();
    }

    public String getIssuor() {
        return payload.path("iss").textValue();
    }

    @Override
    public String toString() {
        return content;
    }
    
    public String toTypedString() {
        return payload.path("typ").textValue() + " " + content;
    }

}
